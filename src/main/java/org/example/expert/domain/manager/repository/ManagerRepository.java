package org.example.expert.domain.manager.repository;

import org.example.expert.domain.manager.dto.response.ManagerQueryDto;
import org.example.expert.domain.manager.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    @Query("""
        SELECT new org.example.expert.domain.manager.dto.response.ManagerQueryDto(m.id, u.id, u.email, m.todo.id)
        FROM Manager m
        JOIN m.user u
        WHERE m.todo.id = :todoId  
    """)
    List<ManagerQueryDto> findByTodoIdWithUser(@Param("todoId") Long todoId);
}
