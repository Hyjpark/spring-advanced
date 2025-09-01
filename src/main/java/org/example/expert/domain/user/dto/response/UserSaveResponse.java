package org.example.expert.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserSaveResponse {

    private final String bearerToken;

    private UserSaveResponse(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public static UserSaveResponse of(String bearerToken) {
        return new UserSaveResponse(bearerToken);
    }
}
