package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

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

    @Test
    void resolveArgument가_Request에서_AuthUser를_꺼낸다(){
        // given
        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        AuthUser authUser = new AuthUser(1L, "asd@asd.com", UserRole.USER);

        given(webRequest.getNativeRequest()).willReturn(servletRequest);
        given(servletRequest.getAttribute("userId")).willReturn(authUser.getId());
        given(servletRequest.getAttribute("email")).willReturn(authUser.getEmail());
        given(servletRequest.getAttribute("userRole")).willReturn(authUser.getUserRole().name());

        // when
        Object result = authUserArgumentResolver.resolveArgument(null, null, webRequest, null);

        // then
        assertEquals(authUser.getId(), ((AuthUser) result).getId());
        assertEquals(authUser.getEmail(), ((AuthUser) result).getEmail());
        assertEquals(authUser.getUserRole(), ((AuthUser) result).getUserRole());
    }
}
