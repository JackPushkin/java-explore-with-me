package ru.practicum.controller.publicapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.model.mapper.CompilationMapper;
import ru.practicum.service.interfaces.CompilationService;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {

    private final CompilationService compilationService;
    private final CompilationMapper mapper;

    @GetMapping
    public List<CompilationDto> getCompilations(
            @RequestParam(value = "pinned", required = false) Boolean pinned,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size
    ) {
        return mapper.toCompilationDtoList(compilationService.getCompilations(pinned, from, size));
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable("compId") @Positive Integer compId) {
        return mapper.toCompilationDto(compilationService.getCompilationById(compId));
    }
}
