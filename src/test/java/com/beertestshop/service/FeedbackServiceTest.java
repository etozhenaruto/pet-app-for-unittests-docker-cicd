//package com.beertestshop.service;
//
//import com.beertestshop.dto.FeedbackDto;
//import com.beertestshop.entity.FeedbackEntity;
//import com.beertestshop.repository.FeedbackRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
///**
// * Тесты для FeedbackService - проверка бизнес-логики управления отзывами.
// *
// * Тестовые кейсы:
// * - Получение всех отзывов
// * - Поиск отзыва по ID
// * - Сохранение нового отзыва
// * - Обновление существующего отзыва
// * - Удаление отзыва
// * - Инициализация тестовых данных
// */
//@ExtendWith(MockitoExtension.class)
//class FeedbackServiceTest {
//
//    @Mock
//    private FeedbackRepository feedbackRepository;
//
//    @InjectMocks
//    private FeedbackService feedbackService;
//
//    private FeedbackEntity testFeedbackEntity;
//    private FeedbackDto testFeedbackDto;
//
//    @BeforeEach
//    void setUp() {
//        testFeedbackEntity = FeedbackEntity.builder()
//                .id(1L)
//                .authorName("Иван Петров")
//                .authorEmail("ivan@example.com")
//                .message("Отличный продукт!")
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        testFeedbackDto = FeedbackDto.builder()
//                .id(1L)
//                .authorName("Иван Петров")
//                .authorEmail("ivan@example.com")
//                .message("Отличный продукт!")
//                .createdAt(LocalDateTime.now())
//                .build();
//    }
//
//    // ==================== findAll() ====================
//
//    @Test
//    @DisplayName("TC-401: Получение всех отзывов - успех")
//    void findAll_Success() {
//        // Подготовка
//        List<FeedbackEntity> entities = Arrays.asList(testFeedbackEntity);
//        given(feedbackRepository.findAll()).willReturn(entities);
//
//        // Действие
//        List<FeedbackDto> result = feedbackService.findAll();
//
//        // Проверка
//        assertThat(result).hasSize(1);
//        assertThat(result.get(0).getAuthorName()).isEqualTo("Иван Петров");
//        verify(feedbackRepository, times(1)).findAll();
//    }
//
//    @Test
//    @DisplayName("TC-402: Получение всех отзывов - пустой список")
//    void findAll_EmptyList() {
//        // Подготовка
//        given(feedbackRepository.findAll()).willReturn(List.of());
//
//        // Действие
//        List<FeedbackDto> result = feedbackService.findAll();
//
//        // Проверка
//        assertThat(result).isEmpty();
//    }
//
//    // ==================== findById() ====================
//
//    @Test
//    @DisplayName("TC-403: Поиск отзыва по ID - успех")
//    void findById_Success() {
//        // Подготовка
//        given(feedbackRepository.findById(1L)).willReturn(Optional.of(testFeedbackEntity));
//
//        // Действие
//        Optional<FeedbackDto> result = feedbackService.findById(1L);
//
//        // Проверка
//        assertThat(result).isPresent();
//        assertThat(result.get().getAuthorName()).isEqualTo("Иван Петров");
//        assertThat(result.get().getId()).isEqualTo(1L);
//    }
//
//    @Test
//    @DisplayName("TC-404: Поиск отзыва по ID - отзыв не найден")
//    void findById_NotFound() {
//        // Подготовка
//        given(feedbackRepository.findById(999L)).willReturn(Optional.empty());
//
//        // Действие
//        Optional<FeedbackDto> result = feedbackService.findById(999L);
//
//        // Проверка
//        assertThat(result).isEmpty();
//    }
//
//    // ==================== save() ====================
//
//    @Test
//    @DisplayName("TC-405: Сохранение нового отзыва - успех")
//    void save_Success() {
//        // Подготовка
//        FeedbackEntity savedEntity = FeedbackEntity.builder()
//                .id(1L)
//                .authorName("Новый пользователь")
//                .authorEmail("new@example.com")
//                .message("Отличный магазин!")
//                .createdAt(LocalDateTime.now())
//                .build();
//        given(feedbackRepository.save(any(FeedbackEntity.class))).willReturn(savedEntity);
//
//        // Действие
//        FeedbackDto result = feedbackService.save(testFeedbackDto);
//
//        // Проверка
//        assertThat(result).isNotNull();
//        verify(feedbackRepository, times(1)).save(any(FeedbackEntity.class));
//    }
//
//    @Test
//    @DisplayName("TC-406: Сохранение отзыва - установка createdAt если null")
//    void save_SetCreatedAtIfNull() {
//        // Подготовка
//        FeedbackDto dtoWithoutDate = FeedbackDto.builder()
//                .authorName("Test")
//                .authorEmail("test@example.com")
//                .message("Test message")
//                .build();
//
//        FeedbackEntity savedEntity = FeedbackEntity.builder()
//                .id(1L)
//                .authorName("Test")
//                .authorEmail("test@example.com")
//                .message("Test message")
//                .createdAt(LocalDateTime.now())
//                .build();
//        given(feedbackRepository.save(any(FeedbackEntity.class))).willReturn(savedEntity);
//
//        // Действие
//        FeedbackDto result = feedbackService.save(dtoWithoutDate);
//
//        // Проверка
//        assertThat(result).isNotNull();
//        verify(feedbackRepository, times(1)).save(any(FeedbackEntity.class));
//    }
//
//    // ==================== update() ====================
//
//    @Test
//    @DisplayName("TC-407: Обновление отзыва - успех")
//    void update_Success() {
//        // Подготовка
//        FeedbackEntity existingEntity = FeedbackEntity.builder()
//                .id(1L)
//                .authorName("Old Name")
//                .authorEmail("old@example.com")
//                .message("Old message")
//                .createdAt(LocalDateTime.now())
//                .build();
//        given(feedbackRepository.findById(1L)).willReturn(Optional.of(existingEntity));
//        given(feedbackRepository.save(any(FeedbackEntity.class))).willReturn(existingEntity);
//
//        FeedbackDto updateDto = FeedbackDto.builder()
//                .authorName("Updated Name")
//                .authorEmail("updated@example.com")
//                .message("Updated message")
//                .build();
//
//        // Действие
//        FeedbackDto result = feedbackService.update(1L, updateDto);
//
//        // Проверка
//        assertThat(result).isNotNull();
//        assertThat(existingEntity.getAuthorName()).isEqualTo("Updated Name");
//        assertThat(existingEntity.getAuthorEmail()).isEqualTo("updated@example.com");
//        assertThat(existingEntity.getMessage()).isEqualTo("Updated message");
//    }
//
//    @Test
//    @DisplayName("TC-408: Обновление отзыва - отзыв не найден")
//    void update_NotFound() {
//        // Подготовка
//        given(feedbackRepository.findById(999L)).willReturn(Optional.empty());
//
//        FeedbackDto updateDto = FeedbackDto.builder()
//                .authorName("Updated")
//                .authorEmail("updated@example.com")
//                .message("Updated message")
//                .build();
//
//        // Действие и проверка
//        assertThatThrownBy(() -> feedbackService.update(999L, updateDto))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Feedback not found");
//    }
//
//    // ==================== delete() ====================
//
//    @Test
//    @DisplayName("TC-409: Удаление отзыва - успех")
//    void delete_Success() {
//        // Подготовка
//        given(feedbackRepository.existsById(1L)).willReturn(true);
//
//        // Действие
//        boolean result = feedbackService.delete(1L);
//
//        // Проверка
//        assertThat(result).isTrue();
//        verify(feedbackRepository, times(1)).deleteById(1L);
//    }
//
//    @Test
//    @DisplayName("TC-410: Удаление отзыва - отзыв не найден")
//    void delete_NotFound() {
//        // Подготовка
//        given(feedbackRepository.existsById(999L)).willReturn(false);
//
//        // Действие
//        boolean result = feedbackService.delete(999L);
//
//        // Проверка
//        assertThat(result).isFalse();
//        verify(feedbackRepository, times(0)).deleteById(999L);
//    }
//
//    // ==================== initTestData() ====================
//
//    @Test
//    @DisplayName("TC-411: Инициализация тестовых данных - отзывы уже существуют")
//    void initTestData_FeedbacksAlreadyExist() {
//        // Подготовка
//        given(feedbackRepository.count()).willReturn(2L);
//
//        // Действие
//        feedbackService.initTestData();
//
//        // Проверка
//        verify(feedbackRepository, times(0)).save(any(FeedbackEntity.class));
//    }
//
//    @Test
//    @DisplayName("TC-412: Инициализация тестовых данных - создание новых отзывов")
//    void initTestData_CreateNewFeedbacks() {
//        // Подготовка
//        given(feedbackRepository.count()).willReturn(0L);
//        given(feedbackRepository.save(any(FeedbackEntity.class))).willReturn(testFeedbackEntity);
//
//        // Действие
//        feedbackService.initTestData();
//
//        // Проверка
//        verify(feedbackRepository, times(2)).save(any(FeedbackEntity.class));
//    }
//}
