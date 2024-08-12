package ru.practicum.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.model.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    Event toEventFromShortDto(EventShortDto eventShortDto);

    Event toEventFromFullDto(EventFullDto eventFullDto);

    EventShortDto toEventShortDto(Event event);

    EventFullDto toEventFullDto(Event event);

    List<Event> toEventListFromShortDtoList(List<EventShortDto> eventShortDtoList);

    List<Event> toEventListFromFullDtoList(List<EventFullDto> eventFullDtoList);

    List<EventShortDto> toEventShortDtoList(List<Event> events);

    List<EventFullDto> toEventFullDtoList(List<Event> events);

    @Mapping(ignore = true, target = "category")
    Event toEventFromNewDto(NewEventDto newEventDto);

    @Mapping(target = "category", source = "category.id")
    NewEventDto toNewEventDto(Event event);
}
