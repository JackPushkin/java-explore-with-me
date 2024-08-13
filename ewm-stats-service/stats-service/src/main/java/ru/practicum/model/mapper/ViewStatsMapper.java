package ru.practicum.model.mapper;


import org.mapstruct.Mapper;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.ViewStats;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ViewStatsMapper {

    ViewStatsDto toViewStatsDto(ViewStats viewStats);

    List<ViewStatsDto> toViewStatsDtoList(List<ViewStats> viewStatsList);
}
