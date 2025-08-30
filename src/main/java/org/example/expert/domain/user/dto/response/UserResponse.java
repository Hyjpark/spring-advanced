package org.example.expert.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserResponse {

    private final Long id;
    private final String email;

    private UserResponse(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public static UserResponse of(Long id, String email) {
        return new UserResponse(id, email);
    }
}
