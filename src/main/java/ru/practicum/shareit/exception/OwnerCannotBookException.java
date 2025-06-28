package ru.practicum.shareit.exception;

public class OwnerCannotBookException extends RuntimeException {
    public OwnerCannotBookException(String message) {
        super(message);
    }
}