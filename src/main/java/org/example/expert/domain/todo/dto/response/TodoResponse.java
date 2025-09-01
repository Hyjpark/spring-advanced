package org.example.expert.domain.todo.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.expert.domain.user.dto.response.UserResponse;

import java.time.LocalDateTime;

@Getter
public class TodoResponse {

    private final Long id;
    private final String title;
    private final String contents;
    private final String weather;
    private final UserResponse user;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    @Builder
    private TodoResponse(Long id, String title, String contents, String weather, UserResponse user, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.weather = weather;
        this.user = user;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static TodoResponse of(Long id, String title, String contents, String weather, UserResponse user, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return TodoResponse.builder()
                .id(id)
                .title(title)
                .contents(contents)
                .weather(weather)
                .user(user)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .build();
    }
}
