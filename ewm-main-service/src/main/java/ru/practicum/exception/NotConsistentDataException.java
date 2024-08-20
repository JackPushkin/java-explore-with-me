package ru.practicum.exception;

public class NotConsistentDataException extends RuntimeException {

    public NotConsistentDataException(String message) {
        super(message);
    }
}
