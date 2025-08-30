package org.example.expert.domain.auth.dto.response;

import lombok.Getter;

@Getter
public class SignupResponse {

    private final String bearerToken;

    private SignupResponse(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public static SignupResponse of(String bearerToken) {
        return new SignupResponse(bearerToken);
    }
}
