package org.example.expert.domain.manager.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.dto.response.UserResponse;

@Getter
public class ManagerSaveResponse {

    private final Long id;
    private final UserResponse user;

    private ManagerSaveResponse(Long id, UserResponse user) {
        this.id = id;
        this.user = user;
    }

    public static ManagerSaveResponse of(Long id, UserResponse user) {
        return new ManagerSaveResponse(id, user);
    }
}
