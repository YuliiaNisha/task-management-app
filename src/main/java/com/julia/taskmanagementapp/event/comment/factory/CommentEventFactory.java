package com.julia.taskmanagementapp.event.comment.factory;

import com.julia.taskmanagementapp.event.MultiFunction;
import com.julia.taskmanagementapp.event.comment.CommentCreatedEvent;
import com.julia.taskmanagementapp.event.comment.CommentEvent;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.model.Comment;
import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentEventFactory {
    private final UserRepository userRepository;
    private final Map<
            CommentEventType,
            MultiFunction<Project, Task, Comment, User, User, CommentEvent>
            > registry =
            Map.of(
                    CommentEventType.COMMENT_CREATED, this::createCommentCreatedEvent
            );

    public CommentEvent create(
            CommentEventType type,
            Project project,
            Task task,
            Comment comment,
            User commentCreator,
            User assignee
    ) {
        MultiFunction<Project, Task, Comment, User, User, CommentEvent> function =
                registry.get(type);

        if (function == null) {
            throw new EntityNotFoundException(
                    "There is no function by name: " + type.name()
            );
        }

        return function.apply(project, task, comment, commentCreator, assignee);
    }

    private CommentEvent createCommentCreatedEvent(
            Project project,
            Task task,
            Comment comment,
            User commentCreator,
            User assignee

    ) {
        return new CommentCreatedEvent(
                getNotificationEmailsNames(project, commentCreator, assignee),
                comment.getId().toString(),
                comment.getText(),
                project.getName(),
                task.getName(),
                String.join(
                        " ",
                        commentCreator.getFirstName(),
                        commentCreator.getLastName())
        );
    }

    private Map<String, String> getNotificationEmailsNames(
            Project project,
            User commentCreator,
            User assignee
    ) {
        User projectCreator = project.getCreator();
        String projectCreatorEmail = projectCreator.getEmail();
        String commentCreatorEmail = commentCreator.getEmail();
        String assigneeEmail = assignee.getEmail();

        Map<String, String> notificationEmailsNames = new HashMap<>();

        if (!commentCreatorEmail.equals(projectCreator.getEmail())) {
            notificationEmailsNames.put(projectCreatorEmail, projectCreator.getFirstName());
        }
        if (!commentCreatorEmail.equals(assigneeEmail)) {
            notificationEmailsNames.put(assigneeEmail, assignee.getFirstName());
        }

        return notificationEmailsNames;
    }
}
