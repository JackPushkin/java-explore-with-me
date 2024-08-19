package ru.practicum.exception;

public class UpdateRequestException extends RuntimeException {

    public UpdateRequestException(String message) {
        super(message);
    }
}
