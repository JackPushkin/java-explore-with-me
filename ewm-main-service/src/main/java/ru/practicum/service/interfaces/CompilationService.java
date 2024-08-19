package ru.practicum.service.interfaces;

import ru.practicum.dto.compilation.UpdateCompilationRequestDto;
import ru.practicum.model.Compilation;

import java.util.List;

public interface CompilationService {

    List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size);

    Compilation getCompilationById(Integer compId);

    Compilation createCompilation(UpdateCompilationRequestDto requestDto);

    void deleteCompilation(Integer compId);

    Compilation updateCompilation(Integer compId, UpdateCompilationRequestDto requestDto);
}
