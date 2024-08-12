package ru.practicum.controller.adminapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequestDto;
import ru.practicum.model.mapper.CompilationMapper;
import ru.practicum.service.interfaces.CompilationService;
import ru.practicum.validation.ValidationMarker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;
    private final CompilationMapper mapper;

    @PostMapping
    @Validated(ValidationMarker.OnCreate.class)
    public ResponseEntity<CompilationDto> createCompilation(@RequestBody @Valid UpdateCompilationRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toCompilationDto(compilationService.createCompilation(requestDto)));
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable("compId") @Positive Integer compId) {
        compilationService.deleteCompilation(compId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(
            @PathVariable("compId") @Positive Integer compId,
            @RequestBody @Valid UpdateCompilationRequestDto requestDto
    ) {
        return mapper.toCompilationDto(compilationService.updateCompilation(compId, requestDto));
    }
}
