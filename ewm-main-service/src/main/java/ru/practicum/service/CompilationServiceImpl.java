package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.UpdateCompilationRequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.RequestStatus;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.interfaces.CompilationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations =
                compilationRepository.findAllByPinnedIn(pinned == null ? List.of(true, false) : List.of(pinned),
                        PageRequest.of(from / size, size)).getContent();
        List<Event> events = compilations.stream().flatMap(compilation -> compilation.getEvents().stream()).collect(Collectors.toList());
        Map<Integer, Long> collect = eventRepository.getRequestIdCountListByEventIdIn(
                RequestStatus.CONFIRMED, events.stream().map(Event::getId).collect(Collectors.toList())).stream()
                .collect(Collectors.toMap(
                        EventRepository.RequestIdCount::getId,
                        EventRepository.RequestIdCount::getConfirmedRequests, Long::sum));
        events.forEach(event -> event.setConfirmedRequests(collect.getOrDefault(event.getId(), 0L)));
        return compilations;
    }

    @Override
    public Compilation getCompilationById(Integer compId) {
        return compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String.format("Compilation id=%d not found", compId)));
    }

    @Override
    public Compilation createCompilation(UpdateCompilationRequestDto requestDto) {
        List<Event> events = eventRepository
                .findAllByIdIn(requestDto.getEvents() == null ? List.of() : requestDto.getEvents());
        Compilation compilation = new Compilation(
                null, requestDto.getPinned() != null && requestDto.getPinned(), requestDto.getTitle(), events);
        return compilationRepository.save(compilation);
    }

    @Override
    public void deleteCompilation(Integer compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException(String.format("Compilation id=%d not found", compId));
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public Compilation updateCompilation(Integer compId, UpdateCompilationRequestDto requestDto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String.format("Compilation id=%d not found", compId)));
        if (requestDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(requestDto.getEvents());
            compilation.setEvents(events);
        }
        compilation.setPinned(requestDto.getPinned() == null ? compilation.isPinned() : requestDto.getPinned());
        compilation.setTitle(requestDto.getTitle() == null ? compilation.getTitle() : requestDto.getTitle());
        return compilationRepository.save(compilation);
    }
}
