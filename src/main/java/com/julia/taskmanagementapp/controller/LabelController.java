package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.label.CreateLabelRequestDto;
import com.julia.taskmanagementapp.dto.label.LabelDto;
import com.julia.taskmanagementapp.dto.label.UpdateLabelRequestDto;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.service.label.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/labels")
public class LabelController {
    private final LabelService labelService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDto create(
            @RequestBody @Valid CreateLabelRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return labelService.create(requestDto, user.getId());
    }

    @GetMapping
    public Page<LabelDto> getLabels(
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        return labelService.getLabels(user.getId(), pageable);
    }

    @PutMapping("/{id}")
    public LabelDto update(
            @PathVariable Long id,
            @RequestBody UpdateLabelRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return labelService.update(id, requestDto, user.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        labelService.delete(id, user.getId());
    }
}
