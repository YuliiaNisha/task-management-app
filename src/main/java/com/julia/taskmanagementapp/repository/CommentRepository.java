package com.julia.taskmanagementapp.repository;

import com.julia.taskmanagementapp.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findCommentsByTaskId(Pageable pageable, Long taskId);
}
