package ru.practicum.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Order(1)
@Component
@RequiredArgsConstructor
public class GetStatsAspect {

    private final StatsClient statsClient;

    @Pointcut("execution(* ru.practicum.controller.publicapi.PublicEventController.getEvents(..))")
    private void publicGetEventsMethod() {
    }

    @Pointcut("execution(* ru.practicum.controller.publicapi.PublicEventController.getEventById(..))")
    private void publicGetEventByIdMethod() {
    }

    @Pointcut("execution(* ru.practicum.controller.publicapi.PublicCompilationController.getCompilations(..))")
    private void getCompilationsMethod() {
    }

    @Pointcut("execution(* ru.practicum.controller.publicapi.PublicCompilationController.getCompilationById(..))")
    private void getCompilationByIdMethod() {
    }

    @Pointcut("execution(* ru.practicum.controller.privateapi.PrivateEventController.getEvents(..))")
    private void privateGetEventsMethod() {
    }

    @Pointcut("execution(* ru.practicum.controller.privateapi.PrivateEventController.getEventById(..))")
    private void privateGetEventByIdMethod() {
    }

    @Pointcut("execution(* ru.practicum.controller.adminapi.AdminEventController.getEventsList(..))")
    private void adminGetEventsMethod() {
    }

    @AfterReturning(value = "publicGetEventByIdMethod() || privateGetEventByIdMethod()", returning = "result")
    public EventFullDto getEventViewsAdvice(JoinPoint joinPoint, EventFullDto result) {
        String uri = "/events/" + result.getId();
        List<ViewStatsDto> list = statsClient.getStats(LocalDateTime.now().minusYears(3000),
                LocalDateTime.now(),
                List.of(uri),
                true).block();
        result.setViews(list == null || list.isEmpty() ? 0 : list.getFirst().getHits().intValue());
        return result;
    }

    @AfterReturning(
            value = "publicGetEventsMethod() || privateGetEventsMethod() || adminGetEventsMethod()",
            returning = "result")
    public List<? extends EventShortDto> getEventListViewsAdvice(JoinPoint joinPoint, List<? extends EventShortDto> result) {
        List<String> uris = result.stream()
                .map(event -> String.format("/events/%d", event.getId()))
                .toList();
        return setViews(result, joinPoint, result);
    }

    @AfterReturning(value = "getCompilationsMethod()", returning = "result")
    public List<CompilationDto> getCompilationsViewsAdvice(JoinPoint joinPoint, List<CompilationDto> result) {
        List<EventShortDto> shortDtos = result.stream()
                .map(CompilationDto::getEvents)
                .flatMap(Collection::stream)
                .toList();
        return setViews(shortDtos, joinPoint, result);
    }

    @AfterReturning(value = "getCompilationByIdMethod()", returning = "result")
    public CompilationDto getCompilationByIdViewsAdvice(JoinPoint joinPoint, CompilationDto result) {
        List<EventShortDto> shortDtos = result.getEvents();
        return setViews(shortDtos, joinPoint, result);
    }

    private <T> T setViews(List<? extends EventShortDto> shortDtos, JoinPoint joinPoint, T result) {
        List<String> uris = shortDtos.stream()
                .map(event -> String.format("/events/%d", event.getId()))
                .toList();
        List<ViewStatsDto> list = statsClient
                .getStats(LocalDateTime.now().minusYears(3000), LocalDateTime.now(), uris, true).block();
        if (list == null || list.isEmpty()) {
            shortDtos.forEach(res -> res.setViews(0));
            return result;
        }
        Map<Integer, Integer> map = list.stream()
                .collect(Collectors.toMap(
                        dto -> Integer.parseInt(dto.getUri().substring(8)),
                        dto -> dto.getHits().intValue()));
        shortDtos.forEach(res -> res.setViews(map.getOrDefault(res.getId(), 0)));
        return result;
    }
}
