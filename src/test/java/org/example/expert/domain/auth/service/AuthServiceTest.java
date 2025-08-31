package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    public void 회원가입이_성공한다() {
        // given
        SignupRequest signupRequest = new SignupRequest("asd@asd.com", "pass", "USER");
        UserRole userRole = UserRole.of(signupRequest.getUserRole());
        User user = User.create(signupRequest.getEmail(), signupRequest.getPassword(), userRole);
        String token = "mockedToken";

        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(jwtUtil.createToken(user.getId(), user.getEmail(), userRole)).willReturn(token);

        // when
        SignupResponse response = authService.signup(signupRequest);

        // then
        assertNotNull(response);
        assertEquals(response.getBearerToken(), token);
    }

    @Test
    public void 이미_존재하는_이메일로_회원가입하면_InvalidRequestException을_던진다() {
        // given
        SignupRequest signupRequest = new SignupRequest("asd@asd.com", "pass", "USER");
        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(true);

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> authService.signup(signupRequest));

        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
    }
}
