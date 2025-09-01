package org.example.expert.domain.comment.repository;

import org.example.expert.domain.comment.dto.response.CommentQueryDto;
import org.example.expert.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
        SELECT new org.example.expert.domain.comment.dto.response.CommentQueryDto(c.id, c.contents, u.id, u.email)
        FROM Comment c
        JOIN c.user u
        WHERE c.todo.id = :todoId
    """)
    List<CommentQueryDto> findByTodoIdWithUser(@Param("todoId") Long todoId);
}
