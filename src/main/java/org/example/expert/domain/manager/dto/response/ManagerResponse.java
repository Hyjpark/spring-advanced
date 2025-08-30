package org.example.expert.domain.manager.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.dto.response.UserResponse;

@Getter
public class ManagerResponse {

    private final Long id;
    private final UserResponse user;

    private ManagerResponse(Long id, UserResponse user) {
        this.id = id;
        this.user = user;
    }

    public static ManagerResponse of(Long id, UserResponse user) {
        return new ManagerResponse(id, user);
    }
}
