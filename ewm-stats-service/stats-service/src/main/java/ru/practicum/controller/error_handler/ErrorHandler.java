package ru.practicum.controller.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.GetStatsException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse getStatsExceptionHandler(GetStatsException e) {
        String reason = "Get statistics error";
        log.error("{}. {}", reason, e.getMessage());
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason(reason)
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
