package ru.practicum.controller.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.exception.*;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentExceptionHandler(Exception e) {
        String reason = "Incorrectly made request";
        return getErrorResponse(HttpStatus.BAD_REQUEST, reason, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundExceptionHandler(NotFoundException e) {
        String reason = "The required object was not found";
        return getErrorResponse(HttpStatus.NOT_FOUND, reason, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse categoryIsNotEmptyException(CategoryIsNotEmptyException e) {
        String reason = "The category is not empty";
        return getErrorResponse(HttpStatus.CONFLICT, reason, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse constraintViolationException(jakarta.validation.ConstraintViolationException e) {
        String reason = "Parameters not valid";
        return getErrorResponse(HttpStatus.BAD_REQUEST, reason, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse constraintViolationException(org.hibernate.exception.ConstraintViolationException e) {
        String reason = "Integrity constraint has been violated";
        return getErrorResponse(HttpStatus.CONFLICT, reason, e.getMessage());
    }

    @ExceptionHandler({ CreateRequestException.class, UpdateRequestException.class })
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse requestExceptionHandler(RuntimeException e) {
        String reason = "Create/Update request error.";
        return getErrorResponse(HttpStatus.CONFLICT, reason, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse updateEventExceptionHandler(UpdateEventException e) {
        String reason = "Update event error.";
        return getErrorResponse(HttpStatus.CONFLICT, reason, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse createCommentExceptionHandler(CreateCommentException e) {
        String reason = "Create comment for not published event.";
        return getErrorResponse(HttpStatus.CONFLICT, reason, e.getMessage());
    }

    private ErrorResponse getErrorResponse(HttpStatus status, String reason, String message) {
        log.error("{}. {}", reason, message);
        return ErrorResponse.builder()
                .status(status)
                .reason(reason)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
