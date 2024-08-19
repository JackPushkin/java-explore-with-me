package ru.practicum.exception;

public class CreateRequestException extends RuntimeException {

    public CreateRequestException(String message) {
        super(message);
    }
}
