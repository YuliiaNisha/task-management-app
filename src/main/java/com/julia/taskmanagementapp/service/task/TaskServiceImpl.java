package com.julia.taskmanagementapp.service.task;

import com.julia.taskmanagementapp.dto.task.CreateTaskRequestDto;
import com.julia.taskmanagementapp.dto.task.TaskDto;
import com.julia.taskmanagementapp.dto.task.UpdateTaskRequestDto;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.mapper.TaskMapper;
import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.repository.ProjectRepository;
import com.julia.taskmanagementapp.repository.TaskRepository;
import com.julia.taskmanagementapp.repository.UserRepository;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public TaskDto create(CreateTaskRequestDto requestDto) {
        entityExistsById(
                requestDto.projectId(),
                projectRepository::existsById,
                "project"
        );
        entityExistsById(
                requestDto.assigneeId(),
                userRepository::existsById,
                "user"
        );
        Task task = taskMapper.toModel(requestDto);
        task.setStatus(Task.Status.NOT_STARTED);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    @Override
    public Page<TaskDto> getTasksForProject(Long projectId, Pageable pageable) {
        entityExistsById(
                projectId,
                projectRepository::existsById,
                "project"
        );
        return taskRepository.findByProjectId(projectId, pageable)
                .map(taskMapper::toDto);
    }

    @Override
    public TaskDto getTaskById(Long id) {
        Task task = findTaskById(id);
        return taskMapper.toDto(task);
    }

    @Override
    public TaskDto update(Long id, UpdateTaskRequestDto requestDto) {
        Task task = findTaskById(id);
        taskMapper.updateTask(task, requestDto);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public void delete(Long id) {
        Task task = findTaskById(id);
        taskRepository.delete(task);
    }

    private void entityExistsById(
            Long id,
            Predicate<Long> existsPredicate,
            String entityName
    ) {
        if (!existsPredicate.test(id)) {
            throw new EntityNotFoundException(
                    "There is no " + entityName + " by id: " + id
            );
        }
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no task by id: " + id)
        );
    }
}
