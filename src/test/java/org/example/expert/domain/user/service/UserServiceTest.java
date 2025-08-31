package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void 존재하는_userId로_조회하면_UserResponse를_반환한다() {
        // given
        String email = "asd@asd.com";
        long userId = 1L;
        User user = User.create(email, "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        UserResponse userResponse = userService.getUser(userId);

        // then
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getId()).isEqualTo(userId);
        assertThat(userResponse.getEmail()).isEqualTo(email);
    }

    @Test
    public void 존재하지_않는_userId로_조회하면_InvalidRequestException을_던진다() {
        // Given
        long userId = 1L;
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThrows(InvalidRequestException.class,
                () -> userService.getUser(userId),
                "User not found"
        );
    }

    @Test
    public void 비밀번호를_정상적으로_변경한다() {
        // given
        long userId = 1L;
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        User user = User.create("asd@asd.com", oldPassword, UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(oldPassword, newPassword);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(newPassword, user.getPassword())).willReturn(false);
        given(passwordEncoder.matches(oldPassword, user.getPassword())).willReturn(true);
        given(passwordEncoder.encode(newPassword)).willReturn("encodedNewPass");

        // when
        userService.changePassword(userId, userChangePasswordRequest);

        // then
        assertEquals(user.getPassword(), "encodedNewPass");
    }

    @Test
    public void 새_비밀번호가_기존_비밀번호와_같으면_InvalidRequestException을_던진다() {
        // given
        long userId = 1L;
        String oldPassword = "password";
        String newPassword = oldPassword;

        User user = User.create("asd@asd.com", oldPassword, UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(oldPassword, newPassword);

        // user가 존재함
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        // 새 비밀번호가 기존과 같음
        given(passwordEncoder.matches(oldPassword, user.getPassword())).willReturn(true);

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.changePassword(userId, userChangePasswordRequest));

        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
    }

    @Test
    public void 기존_비밀번호가_틀리면_InvalidRequestException을_던진다() {
        // given
        long userId = 1L;
        String oldPassword = "password";
        String newPassword = "newPassword";

        User user = User.create("asd@asd.com", "pass", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(oldPassword, newPassword);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), eq(user.getPassword()))).willReturn(false);

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.changePassword(userId, userChangePasswordRequest));

        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }

    @Test
    public void 존재하는_managerUserId로_조회하면_User를_반환한다() {
        // given
        long managerUserId = 1L;
        User user = User.create("asd@asd.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", managerUserId);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        User managerUser = userService.getManagerUserById(managerUserId);

        // Then
        assertThat(managerUser).isNotNull();
        assertEquals(managerUser.getId(), user.getId());
    }

    @Test
    public void 존재하지_않는_managerUserId로_조회하면_InvalidRequestException을_던진다() {
        // given
        long managerUserId = 1L;
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.getManagerUserById(managerUserId));

        assertEquals("등록하려고 하는 담당자 유저가 존재하지 않습니다.", exception.getMessage());
    }
}
