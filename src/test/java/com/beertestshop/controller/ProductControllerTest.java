package com.beertestshop.controller;

import com.beertestshop.dto.ProductDto;
import com.beertestshop.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для ProductController - проверка всех эндпоинтов управления продуктами.
 * 
 * Тестовые кейсы:
 * - Получение всех активных продуктов
 * - Получение всех продуктов (включая неактивные)
 * - Получение продукта по ID
 * - Создание нового продукта
 * - Обновление существующего продукта
 * - Переключение статуса продукта
 * - Удаление продукта
 * - Обработка ошибок (продукт не найден, валидация)
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductDto testProduct;

    @BeforeEach
    void setUp() {
        testProduct = ProductDto.builder()
                .id(1L)
                .name("Test Lager")
                .description("Test Description")
                .price(new BigDecimal("150.00"))
                .quantity(10)
                .isActive(true)
                .imageUrl("https://example.com/test.jpg")
                .build();
    }

    // ==================== GET /api/v1/products ====================

    @Test
    @DisplayName("TC-001: Получение всех активных продуктов - успех")
    void getAllProducts_Success() throws Exception {
        // Подготовка
        List<ProductDto> products = Arrays.asList(testProduct);
        given(productService.findAllActive()).willReturn(products);

        // Действие и проверка
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Lager"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].price").value(150.00))
                .andExpect(jsonPath("$[0].quantity").value(10))
                .andExpect(jsonPath("$[0].isActive").value(true))
                .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/test.jpg"));

        verify(productService, times(1)).findAllActive();
    }

    @Test
    @DisplayName("TC-002: Получение всех активных продуктов - пустой список")
    void getAllProducts_EmptyList() throws Exception {
        // Подготовка
        given(productService.findAllActive()).willReturn(List.of());

        // Действие и проверка
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ==================== GET /api/v1/products/all ====================

    @Test
    @DisplayName("TC-003: Получение всех продуктов (включая неактивные) - успех")
    void getAllProductsIncludingInactive_Success() throws Exception {
        // Подготовка
        ProductDto inactiveProduct = ProductDto.builder()
                .id(2L)
                .name("Inactive Beer")
                .description("Inactive")
                .price(new BigDecimal("100.00"))
                .quantity(0)
                .isActive(false)
                .imageUrl("https://example.com/inactive.jpg")
                .build();
        List<ProductDto> products = Arrays.asList(testProduct, inactiveProduct);
        given(productService.findAll()).willReturn(products);

        // Действие и проверка
        mockMvc.perform(get("/api/v1/products/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].isActive").value(true))
                .andExpect(jsonPath("$[1].isActive").value(false));
    }

    // ==================== GET /api/v1/products/{id} ====================

    @Test
    @DisplayName("TC-004: Получение продукта по ID - успех")
    void getProductById_Success() throws Exception {
        // Подготовка
        given(productService.findById(1L)).willReturn(Optional.of(testProduct));

        // Действие и проверка
        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Lager"))
                .andExpect(jsonPath("$.price").value(150.00))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    @DisplayName("TC-005: Получение продукта по ID - продукт не найден")
    void getProductById_NotFound() throws Exception {
        // Подготовка
        given(productService.findById(999L)).willReturn(Optional.empty());

        // Действие и проверка
        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound());
    }

    // ==================== POST /api/v1/products ====================

    @Test
    @DisplayName("TC-006: Создание нового продукта - успех")
    void createProduct_Success() throws Exception {
        // Подготовка
        ProductDto createdProduct = ProductDto.builder()
                .id(1L)
                .name("New Beer")
                .description("New Description")
                .price(new BigDecimal("200.00"))
                .quantity(5)
                .isActive(true)
                .imageUrl("https://example.com/new.jpg")
                .build();
        given(productService.save(any(ProductDto.class))).willReturn(createdProduct);

        // Действие и проверка
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Beer"))
                .andExpect(jsonPath("$.price").value(200.00));

        // Проверка
        verify(productService, times(1)).save(any(ProductDto.class));
    }

    @Test
    @DisplayName("TC-007: Создание продукта - валидация: пустое имя")
    void createProduct_Validation_EmptyName() throws Exception {
        // Подготовка
        ProductDto invalidProduct = ProductDto.builder()
                .name("")
                .price(new BigDecimal("100.00"))
                .quantity(5)
                .isActive(true)
                .build();

        // Действие и проверка
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-008: Создание продукта - валидация: отрицательная цена")
    void createProduct_Validation_NegativePrice() throws Exception {
        // Подготовка
        ProductDto invalidProduct = ProductDto.builder()
                .name("Test Beer")
                .price(new BigDecimal("-10.00"))
                .quantity(5)
                .isActive(true)
                .build();

        // Действие и проверка
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-009: Создание продукта - валидация: отрицательное количество")
    void createProduct_Validation_NegativeQuantity() throws Exception {
        // Подготовка
        ProductDto invalidProduct = ProductDto.builder()
                .name("Test Beer")
                .price(new BigDecimal("100.00"))
                .quantity(-5)
                .isActive(true)
                .build();

        // Действие и проверка
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    // ==================== PUT /api/v1/products/{id} ====================

    @Test
    @DisplayName("TC-010: Обновление продукта - успех")
    void updateProduct_Success() throws Exception {
        // Подготовка
        ProductDto updatedProduct = ProductDto.builder()
                .id(1L)
                .name("Updated Beer")
                .description("Updated Description")
                .price(new BigDecimal("250.00"))
                .quantity(15)
                .isActive(true)
                .imageUrl("https://example.com/updated.jpg")
                .build();
        given(productService.update(eq(1L), any(ProductDto.class))).willReturn(updatedProduct);

        // Действие и проверка
        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Beer"))
                .andExpect(jsonPath("$.price").value(250.00));
    }

    @Test
    @DisplayName("TC-011: Обновление продукта - продукт не найден")
    void updateProduct_NotFound() throws Exception {
        // Подготовка
        ProductDto product = ProductDto.builder()
                .id(999L)
                .name("Not Found")
                .price(new BigDecimal("100.00"))
                .quantity(5)
                .isActive(true)
                .build();
        given(productService.update(eq(999L), any(ProductDto.class)))
                .willThrow(new RuntimeException("Product not found"));

        // Действие и проверка
        mockMvc.perform(put("/api/v1/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound());
    }

    // ==================== PUT /api/v1/products/{id}/toggle-status ====================

    @Test
    @DisplayName("TC-012: Переключение статуса продукта - успех")
    void toggleProductStatus_Success() throws Exception {
        // Подготовка
        ProductDto toggledProduct = ProductDto.builder()
                .id(1L)
                .name("Test Lager")
                .description("Test")
                .price(new BigDecimal("150.00"))
                .quantity(10)
                .isActive(false) // Статус изменен на false
                .imageUrl("https://example.com/test.jpg")
                .build();
        given(productService.findById(1L)).willReturn(Optional.of(testProduct));
        given(productService.update(eq(1L), any(ProductDto.class))).willReturn(toggledProduct);

        // Действие и проверка
        mockMvc.perform(put("/api/v1/products/1/toggle-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    @DisplayName("TC-013: Переключение статуса - продукт не найден")
    void toggleProductStatus_NotFound() throws Exception {
        // Подготовка
        given(productService.findById(999L)).willReturn(Optional.empty());

        // Действие и проверка
        mockMvc.perform(put("/api/v1/products/999/toggle-status"))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE /api/v1/products/{id} ====================

    @Test
    @DisplayName("TC-014: Удаление продукта - успех")
    void deleteProduct_Success() throws Exception {
        // Подготовка
        given(productService.delete(1L)).willReturn(true);

        // Действие и проверка
        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());

        // Проверка
        verify(productService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("TC-015: Удаление продукта - продукт не найден")
    void deleteProduct_NotFound() throws Exception {
        // Подготовка
        given(productService.delete(999L)).willReturn(false);

        // Действие и проверка
        mockMvc.perform(delete("/api/v1/products/999"))
                .andExpect(status().isNotFound());
    }
}
