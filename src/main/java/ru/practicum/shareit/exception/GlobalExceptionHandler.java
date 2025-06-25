package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST контроллеров.
 * Перехватывает различные исключения и формирует соответствующие HTTP ответы с сообщениями об ошибках.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка исключения IllegalArgumentException.
     * Возвращает статус 400 Bad Request с сообщением об ошибке.
     * @param e исключение
     * @return объект с сообщением об ошибке
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Validation error: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка исключения ConflictException.
     * Возвращает статус 409 Conflict с сообщением об ошибке.
     * @param e исключение
     * @return объект с сообщением об ошибке
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException e) {
        log.warn("Conflict error: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.CONFLICT);
    }

    /**
     * Обработка исключения NotAvailableException.
     * Возвращает статус 400 Bad Request с сообщением об ошибке.
     * @param e исключение
     * @return объект с сообщением об ошибке
     */
    @ExceptionHandler(NotAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotAvailableException(NotAvailableException e) {
        log.warn("Не доступно для бронирования: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обработка исключения UserNotFoundException.
     * Возвращает статус 404 Not Found с сообщением об ошибке.
     * @param e исключение
     * @return объект с сообщением об ошибке
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        log.warn("Пользователь не найден: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обработка исключения ItemNotFoundException.
     * Возвращает статус 404 Not Found с сообщением об ошибке.
     * @param e исключение
     * @return объект с сообщением об ошибке
     */
    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFoundException(ItemNotFoundException e) {
        log.warn("Вещь не найдена: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обработка исключения BookingNotFoundException.
     * Возвращает статус 404 Not Found с сообщением об ошибке.
     * @param e исключение
     * @return объект с сообщением об ошибке
     */
    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotFoundException(BookingNotFoundException e) {
        log.warn("Бронирование не найдено: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обработка исключения OwnerCannotBookException.
     * Возвращает статус 400 Bad Request с сообщением об ошибке.
     * @param e исключение
     * @return объект с сообщением об ошибке
     */
    @ExceptionHandler(OwnerCannotBookException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleOwnerCannotBookException(OwnerCannotBookException e) {
        log.warn("Владелец не может бронировать: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обработка исключения AccessDeniedException.
     * Возвращает статус 403 Forbidden с сообщением об ошибке.
     * @param e исключение
     * @return объект с сообщением об ошибке
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Доступ запрещён: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обработка исключения InvalidBookingStateException.
     * Возвращает статус 400 Bad Request с сообщением об ошибке.
     * @param e исключение
     * @return объект с сообщением об ошибке
     */
    @ExceptionHandler(InvalidBookingStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidBookingStateException(InvalidBookingStateException e) {
        log.warn("Недопустимое состояние бронирования: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обработка ошибок валидации аргументов метода.
     * Собирает все ошибки валидации в карту и возвращает с HTTP статусом 400.
     * @param ex исключение валидации
     * @return карта с ошибками валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Validation errors: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка исключения HttpMessageNotReadableException.
     * Возвращает статус 400 Bad Request с сообщением о некорректном формате данных.
     * @param e исключение
     * @return объект с сообщением об ошибке
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("Invalid request body: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse("Некорректный формат данных в запросе"), HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка всех остальных необработанных исключений.
     * Возвращает статус 500 Internal Server Error с сообщением об ошибке.
     * @param e исключение
     * @return объект с сообщением об ошибке
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(Exception e) {
        log.error("Произошла непредвиденная ошибка: {}", e.getMessage(), e);
        return new ErrorResponse("Unexpected error: " + e.getMessage());
    }

    /**
     * Обработка исключения NotFoundException.
     * Возвращает статус 404 Not Found с сообщением об ошибке.
     * @param e исключение
     * @return объект с сообщением об ошибке
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.warn("Не найдено: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}