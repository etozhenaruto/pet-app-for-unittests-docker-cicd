package com.beertestshop.controller;

import com.beertestshop.dto.AddToCartRequest;
import com.beertestshop.dto.CartDto;
import com.beertestshop.dto.CartItemDto;
import com.beertestshop.service.CartService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для CartController - проверка всех эндпоинтов управления корзиной.
 * Тестовые кейсы:
 * - Получение корзины
 * - Добавление товара в корзину
 * - Удаление товара из корзины
 * - Очистка корзины
 * - Валидация данных (количество товара, ID продукта)
 * - Обработка ошибок (товар не найден, недостаточно на складе)
 */
@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    private CartDto emptyCart;
    private CartDto cartWithItems;
    private CartDto cardWithAddingItems;

    @BeforeEach
    void setUp() {
        // Пустая корзина
        emptyCart = CartDto.builder()
                .items(List.of())
                .totalQuantity(0)
                .totalPrice(BigDecimal.ZERO)
                .build();

        // Корзина с товарами
        CartItemDto item1 = CartItemDto.builder()
                .productId(1L)
                .productName("Beer")
                .quantity(2)
                .price(new BigDecimal("150.00"))
                .total(new BigDecimal("300.00"))
                .build();

        CartItemDto item2 = CartItemDto.builder()
                .productId(2L)
                .productName("Pivchik")
                .quantity(1)
                .price(new BigDecimal("180.00"))
                .total(new BigDecimal("180.00"))
                .build();
        
        CartItemDto  item3 = CartItemDto.builder()
                .productId(3L)
                .productName("Pivasik")
                .quantity(6)
                .price(new BigDecimal("228"))
                .total(new BigDecimal("1337"))
                .build();
        
        cartWithItems = CartDto.builder()
                .items(List.of(item1, item2, item3))
                .totalQuantity(3)
                .totalPrice(new BigDecimal("480.00"))
                .build();

        cardWithAddingItems = CartDto.builder()
                .items(List.of(item1))
                .totalQuantity(1)
                .totalPrice(new BigDecimal("300"))
                .build();

    }

    // ==================== GET /api/v1/cart ====================

    @Test
    @DisplayName("TC-201: Получение корзины - пустая корзина")
    void getCart_Empty() throws Exception {
        // Настройка мока, что будет возвращать mockMvc
        given(cartService.getCart()).willReturn(emptyCart);

        // Имитация запроса и проверка респонса
        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalQuantity").value(0))
                .andExpect(jsonPath("$.totalPrice").value(0));

        // Проверка что метод вызывался не более одного раза  что метод вызывался не более одного раза 
        verify(cartService, times(1)).getCart();
    }

    @Test
    @DisplayName("TC-202: Получение корзины - корзина с товарами")
    void getCart_WithItems() throws Exception {
        // Настройка мока, что будет возвращать mockMvc
        given(cartService.getCart()).willReturn(cartWithItems);

        // Имитация запроса и проверка респонса
        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(3))
                .andExpect(jsonPath("$.totalQuantity").value(3))
                .andExpect(jsonPath("$.totalPrice").value(480.00))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].productName").value("Beer"))
                .andExpect(jsonPath("$.items[0].price").value(150))
                .andExpect(jsonPath("$.items[0].total").value(300))
                .andExpect(jsonPath("$.items[1].productId").value(2))
                .andExpect(jsonPath("$.items[1].quantity").value(1))
                .andExpect(jsonPath("$.items[1].productName").value("Pivchik"))
                .andExpect(jsonPath("$.items[1].price").value(180))
                .andExpect(jsonPath("$.items[1].total").value(180))
                .andExpect(jsonPath("$.items[2].productId").value(3))
                .andExpect(jsonPath("$.items[2].quantity").value(6))
                .andExpect(jsonPath("$.items[2].productName").value("Pivasik"))
                .andExpect(jsonPath("$.items[2].price").value(228))
                .andExpect(jsonPath("$.items[2].total").value(1337));

        // Проверка что метод вызывался не более одного раза  что метод вызывался не более одного раза 
        verify(cartService, times(1)).getCart();

    }

    // ==================== POST /api/v1/cart/add ====================

    @Test
    @DisplayName("TC-203: Добавление товара в корзину - успех")
    void addToCart_Success() throws Exception {
        // Обьект для реквеста
        AddToCartRequest request = new AddToCartRequest(1L, 2);

        // Настройка мока, что будет возвращать mockMvc
        given(cartService.addItem(1L,2)).willReturn(cardWithAddingItems);

        // Имитация запроса и проверка респонса
        mockMvc.perform(post("/api/v1/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].productName").value("Beer"))
                .andExpect(jsonPath("$.items[0].price").value(150))
                .andExpect(jsonPath("$.items[0].total").value(300))
                .andExpect(jsonPath("$.totalQuantity").value(1))
                .andExpect(jsonPath("$.totalPrice").value(300));

        // Проверка что метод вызывался не более одного раза 
        verify(cartService, times(1)).addItem(1L,  2);
    }


    //TODO разобраться нужны ли вообще юнит тесты на проверку валидации спрингом или мы ему верим и такое не проверяем
    @Test
    @DisplayName("TC-204: Добавление товара - валидация: нулевое количество")
    void addToCart_Validation_ZeroQuantity() throws Exception {
        // Обьект для реквеста
        AddToCartRequest request = new AddToCartRequest(1L, 0);

        // Имитация запроса и проверка респонса ( установлена валидация на дто
        // @Min(value = 1, message = "Количество должно быть больше 0"
        // private Integer quantity;)
        mockMvc.perform(post("/api/v1/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    //TODO разобраться нужны ли вообще юнит тесты на проверку валидации спрингом или мы ему верим и такое не проверяем
    @Test
    @DisplayName("TC-205: Добавление товара - валидация: отрицательное количество")
    void addToCart_Validation_NegativeQuantity() throws Exception {
        // Обьект для реквеста
        AddToCartRequest request = new AddToCartRequest(1L, -5);

        // Имитация запроса и проверка респонса ( установлена валидация на дто
        // @Min(value = 1, message = "Количество должно быть больше 0"
        // private Integer quantity;)
        mockMvc.perform(post("/api/v1/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-206: Добавление товара - ошибка: товар не найден")
    void addToCart_ProductNotFound() throws Exception {
        // Обьект для реквеста
        AddToCartRequest request = new AddToCartRequest(999L, 1);

        // Настройка мока, что будет возвращать mockMvc
        given(cartService.addItem(999L, 1))
                .willThrow(new IllegalArgumentException("Товар не найден с id: 999"));

        // Имитация запроса и проверка респонса - IllegalArgumentException обрабатывается как 400 Bad Request
        mockMvc.perform(post("/api/v1/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Товар не найден с id: 999"));
    }
    @Test
    @DisplayName("TC-207: Добавление товара - ошибка: недостаточно товара на складе")
    void addToCart_InsufficientStock() throws Exception {
        // Обьект для реквеста
        AddToCartRequest request = new AddToCartRequest(1L, 100);
        // Настройка мока, что будет возвращать mockMvc
        given(cartService.addItem(1L,100))
                .willThrow(new IllegalArgumentException("Недостаточно товара на складе. Доступно: 5 шт."));
        // Имитация запроса и проверка респонса - IllegalArgumentException обрабатывается как 400 Bad Request
        mockMvc.perform(post("/api/v1/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Недостаточно товара на складе. Доступно: 5 шт."));
    }

    // ==================== DELETE /api/v1/cart/remove ====================

    @Test
    @DisplayName("TC-208: Удаление товара из корзины - успех")
    void removeFromCart_Success() throws Exception {
        // Настройка мока, что будет возвращать mockMvc
        given(cartService.removeItem( 1L)).willReturn(emptyCart);

        // Имитация запроса и проверка респонса
        mockMvc.perform(delete("/api/v1/cart/remove")
                        .param("productId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalQuantity").value(0));

        // Проверка что метод вызывался не более одного раза
        verify(cartService, times(1)).removeItem( 1L);
    }

    // ==================== DELETE /api/v1/cart/clear ====================

    @Test
    @DisplayName("TC-209: Очистка корзины - успех")
    void clearCart_Success() throws Exception {
        // Настройка мока, что будет возвращать mockMvc
        given(cartService.clearCart()).willReturn(emptyCart);
        // Имитация запроса и проверка респонса
        mockMvc.perform(delete("/api/v1/cart/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalQuantity").value(0))
                .andExpect(jsonPath("$.totalPrice").value(0));
        // Проверка что метод вызывался не более одного раза
        verify(cartService, times(1)).clearCart();
    }


}
