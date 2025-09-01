package org.example.expert.config;

import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthUserArgumentResolverTest {

    @InjectMocks
    private AuthUserArgumentResolver authUserArgumentResolver;

    @Test
    void AuthUser_어노테이션있고_타입맞으면_true를_반환한다() {
        // given
        MethodParameter parameter = mock(MethodParameter.class);
        given(parameter.getParameterAnnotation(Auth.class)).willReturn(mock(Auth.class));
        given(parameter.getParameterType()).willAnswer(invocation -> AuthUser.class);

        // when
        boolean result = authUserArgumentResolver.supportsParameter(parameter);

        // then
        assertTrue(result);
    }

    @Test
    void AuthUser_어노테이션있고_타입틀리면_false를_반환한다() {
        // given
        MethodParameter parameter = mock(MethodParameter.class);
        given(parameter.getParameterAnnotation(Auth.class)).willReturn(null);
        given(parameter.getParameterType()).willAnswer(invocation -> String.class);

        // when
        boolean result = authUserArgumentResolver.supportsParameter(parameter);

        // then
        assertFalse(result);
    }

    @Test
    void AuthUser_어노테이션_타입불일치하면_AuthException를_던진다() {
        // given
        MethodParameter parameter = mock(MethodParameter.class);
        given(parameter.getParameterAnnotation(Auth.class)).willReturn(mock(Auth.class));
        given(parameter.getParameterType()).willAnswer(invocation -> String.class);

        // when & then
        AuthException exception = assertThrows(AuthException.class,
                () -> authUserArgumentResolver.supportsParameter(parameter));

        assertEquals("@Auth와 AuthUser 타입은 함께 사용되어야 합니다.", exception.getMessage());
    }
}
