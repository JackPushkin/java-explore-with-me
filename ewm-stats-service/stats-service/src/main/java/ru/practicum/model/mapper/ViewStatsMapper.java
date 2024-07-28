package ru.practicum.model.mapper;

import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.ViewStats;

import java.util.List;
import java.util.stream.Collectors;

public class ViewStatsMapper {

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }

    public static List<ViewStatsDto> toViewStatsDto(List<ViewStats> viewStatsList) {
        return viewStatsList.stream()
                .map(ViewStatsMapper::toViewStatsDto)
                .collect(Collectors.toList());
    }
}
