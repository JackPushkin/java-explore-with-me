package ru.practicum.exception;

public class InconsistentDataException extends RuntimeException {

    public InconsistentDataException(String message) {
        super(message);
    }
}
