package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.label.CreateLabelRequestDto;
import com.julia.taskmanagementapp.dto.label.LabelDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/labels")
public class LabelController {
    @PostMapping
    public LabelDto create(CreateLabelRequestDto requestDto) {
        return null;
    }
}
