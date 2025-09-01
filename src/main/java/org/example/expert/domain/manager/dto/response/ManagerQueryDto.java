package org.example.expert.domain.manager.dto.response;

import lombok.Getter;

@Getter
public class ManagerQueryDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private Long todoId;

    public ManagerQueryDto(Long id, Long userId, String userEmail, Long todoId) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.todoId = todoId;
    }
}
