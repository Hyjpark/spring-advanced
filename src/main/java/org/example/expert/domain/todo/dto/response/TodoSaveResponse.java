package org.example.expert.domain.todo.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.expert.domain.user.dto.response.UserResponse;

@Getter
public class TodoSaveResponse {

    private final Long id;
    private final String title;
    private final String contents;
    private final String weather;
    private final UserResponse user;

    @Builder
    private TodoSaveResponse(Long id, String title, String contents, String weather, UserResponse user) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.weather = weather;
        this.user = user;
    }

    public static TodoSaveResponse of(Long id, String title, String contents, String weather, UserResponse user) {
        return TodoSaveResponse.builder()
                .id(id)
                .title(title)
                .contents(contents)
                .weather(weather)
                .user(user)
                .build();
    }
}
