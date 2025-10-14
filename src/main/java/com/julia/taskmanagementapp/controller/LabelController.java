package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.label.CreateLabelRequestDto;
import com.julia.taskmanagementapp.dto.label.LabelDto;
import com.julia.taskmanagementapp.service.label.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
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
    public LabelDto create(@RequestBody @Valid CreateLabelRequestDto requestDto) {
        return labelService.create(requestDto);
    }
}
