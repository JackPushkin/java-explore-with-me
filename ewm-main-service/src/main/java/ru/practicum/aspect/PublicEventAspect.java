package ru.practicum.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PublicEventAspect {

    private final StatsClient statsClient;

    @Pointcut("execution(* ru.practicum.controller.publicapi.PublicEventController.getEvents(..))")
    private void getEventsMethod() {
    }

    @Pointcut("execution(* ru.practicum.controller.publicapi.PublicEventController.getEventById(..))")
    private void getEventsByIdMethod() {
    }

    @AfterReturning(value = "getEventsMethod() || getEventsByIdMethod()")
    public void doAccessCheck(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        HttpServletRequest request = (HttpServletRequest) args[0];
        String clientIp = request.getRemoteAddr();
        String endpointPath = request.getRequestURI();
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .ip(clientIp)
                .uri(endpointPath)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        statsClient.postHit(endpointHitDto).block();
    }
}
