package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.task.CreateTaskRequestDto;
import com.julia.taskmanagementapp.dto.task.TaskDto;
import com.julia.taskmanagementapp.dto.task.UpdateTaskRequestDto;
import com.julia.taskmanagementapp.service.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/tasks")
@RestController
@RequiredArgsConstructor
class TaskController {
    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto create(@RequestBody @Valid CreateTaskRequestDto requestDto) {
        return taskService.create(requestDto);
    }

    @GetMapping
    public Page<TaskDto> getTasksForProject(
            @RequestParam Long projectId,
            Pageable pageable
    ) {
        return taskService.getTasksForProject(projectId, pageable);
    }

    @GetMapping("/{id}")
    public TaskDto getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    public TaskDto update(
            @PathVariable Long id, @RequestBody @Valid UpdateTaskRequestDto requestDto
    ) {
        return taskService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
