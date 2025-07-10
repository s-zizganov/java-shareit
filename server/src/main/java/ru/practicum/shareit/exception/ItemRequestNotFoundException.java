package ru.practicum.shareit.exception;

/**
 * Исключение, выбрасываемое при попытке доступа к несуществующему запросу на вещь.
 */
public class ItemRequestNotFoundException extends RuntimeException {
    /**
     * Конструктор с сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public ItemRequestNotFoundException(String message) {
        super(message);
    }

    /**
     * Конструктор с сообщением об ошибке и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause   причина исключения
     */
    public ItemRequestNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}