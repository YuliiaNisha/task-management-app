package com.julia.taskmanagementapp.event.task.factory;

import com.julia.taskmanagementapp.event.task.LabelAssignedToTaskEvent;
import com.julia.taskmanagementapp.event.task.LabelRemovedFromTaskEvent;
import com.julia.taskmanagementapp.event.task.TaskCreatedEvent;
import com.julia.taskmanagementapp.event.task.TaskDeletedEvent;
import com.julia.taskmanagementapp.event.task.TaskEvent;
import com.julia.taskmanagementapp.event.task.TaskUpdatedEvent;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.exception.ForbiddenAccessException;
import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.UserRepository;
import java.util.Map;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TaskEventFactory {
    private final UserRepository userRepository;
    private final Map<TaskEventType, BiFunction<Project, Task, TaskEvent>> registry =
            Map.of(
                    TaskEventType.TASK_CREATED, this::createTaskCreatedEvent,
                    TaskEventType.TASK_UPDATED, this::createTaskUpdatedEvent,
                    TaskEventType.TASK_DELETED, this::createTaskDeletedEvent,
                    TaskEventType.LABEL_ASSIGNED_TO_TASK, this::createLabelAssignedEvent,
                    TaskEventType.LABEL_REMOVED_FROM_TASK, this::createLabelRemovedEvent
            );

    public TaskEvent create(TaskEventType type, Project project, Task task) {
        BiFunction<Project, Task, TaskEvent> function = registry.get(type);

        if (function == null) {
            throw new EntityNotFoundException(
                    "There is no function by name: " + type.name()
            );
        }

        return function.apply(project, task);
    }

    private TaskEvent createLabelRemovedEvent(Project project,Task task) {
        User assignee = getAssignee(task.getAssigneeId());
        return new LabelRemovedFromTaskEvent(
                assignee.getEmail(),
                assignee.getFirstName(),
                task.getId().toString(),
                task.getName(),
                project.getName(),
                task.getDueDate().toString()
        );
    }

    private TaskEvent createLabelAssignedEvent(Project project,Task task) {
        User assignee = getAssignee(task.getAssigneeId());
        return new LabelAssignedToTaskEvent(
                assignee.getEmail(),
                assignee.getFirstName(),
                task.getId().toString(),
                task.getName(),
                project.getName(),
                task.getDueDate().toString()
        );
    }

    private TaskEvent createTaskDeletedEvent(Project project,Task task) {
        User assignee = getAssignee(task.getAssigneeId());
        return new TaskDeletedEvent(
                assignee.getEmail(),
                assignee.getFirstName(),
                task.getName(),
                project.getName()
        );
    }

    private TaskEvent createTaskUpdatedEvent(Project project,Task task) {
        User assignee = getAssignee(task.getAssigneeId());
        return new TaskUpdatedEvent(
                assignee.getEmail(),
                assignee.getFirstName(),
                task.getId().toString(),
                task.getName(),
                project.getName(),
                task.getDueDate().toString()
        );
    }

    private TaskEvent createTaskCreatedEvent(Project project,Task task) {
        User assignee = getAssignee(task.getAssigneeId());
        return new TaskCreatedEvent(
                assignee.getEmail(),
                assignee.getFirstName(),
                task.getId().toString(),
                task.getName(),
                project.getName(),
                task.getDueDate().toString()
        );
    }

    private User getAssignee(Long assigneeId) {
        return userRepository.findById(assigneeId).orElseThrow(
                () -> new ForbiddenAccessException(
                        "There is no user by id: " + assigneeId
                )
        );
    }
}
