package ru.practicum.shareit.user;

/**
 * Класс для представления ответа с ошибкой.
 * Содержит сообщение об ошибке, которое будет отправлено клиенту.
 */
public class ErrorResponse {
    /**
     * Сообщение об ошибке.
     */
    private String message;

    /**
     * Создает новый экземпляр ErrorResponse.
     * @param message сообщение об ошибке.
     */
    public ErrorResponse(String message) {
        this.message = message;
    }

    /**
     * Возвращает сообщение об ошибке.
     * @return сообщение об ошибке.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Устанавливает сообщение об ошибке.
     * @param message новое сообщение об ошибке.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}