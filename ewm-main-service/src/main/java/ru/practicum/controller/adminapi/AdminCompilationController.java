package ru.practicum.controller.adminapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequestDto;
import ru.practicum.model.mapper.CompilationMapper;
import ru.practicum.service.interfaces.CompilationService;
import ru.practicum.validation.ValidationMarker;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;
    private final CompilationMapper mapper;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @Validated(ValidationMarker.OnCreate.class)
    public CompilationDto createCompilation(@RequestBody @Valid UpdateCompilationRequestDto requestDto) {
        return mapper.toCompilationDto(compilationService.createCompilation(requestDto));
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Positive Integer compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    @Validated(ValidationMarker.OnUpdate.class)
    public CompilationDto updateCompilation(
            @PathVariable @Positive(groups = ValidationMarker.OnUpdate.class) Integer compId,
            @RequestBody @Valid UpdateCompilationRequestDto requestDto
    ) {
        return mapper.toCompilationDto(compilationService.updateCompilation(compId, requestDto));
    }
}
