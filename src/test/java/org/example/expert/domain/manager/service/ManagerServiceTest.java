package org.example.expert.domain.manager.service;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerQueryDto;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private UserService userService;
    @Mock
    private TodoService todoService;
    @InjectMocks
    private ManagerService managerService;

    @Test
    public void manager_목록_조회_시_Todo가_없다면_InvalidRequestException_에러를_던진다() {
        // given
        long todoId = 1L;
        given(managerRepository.findByTodoIdWithUser(anyLong())).willReturn(Collections.emptyList());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> managerService.getManagers(todoId));
        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    void todo의_user가_null인_경우_예외가_발생한다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        long todoId = 1L;
        long managerUserId = 2L;

        Todo todo = Todo.create("", "", "", null);
        ReflectionTestUtils.setField(todo, "user", null);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);

        given(todoService.getTodoById(todoId)).willReturn(todo);

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
            managerService.saveManager(authUser, todoId, managerSaveRequest)
        );

        assertEquals("일정을 생성한 유저만 담당자를 지정할 수 있습니다.", exception.getMessage());
    }

    @Test // 테스트코드 샘플
    public void manager_목록_조회에_성공한다() {
        // given
        long todoId = 1L;
        ManagerQueryDto mockManagerDto = new ManagerQueryDto(1L, 1L, "a@a.com", todoId);

        List<ManagerQueryDto> managerList = List.of(mockManagerDto);

        given(managerRepository.findByTodoIdWithUser(todoId)).willReturn(managerList);

        // when
        List<ManagerResponse> managerResponses = managerService.getManagers(todoId);

        // then
        assertEquals(1, managerResponses.size());
        assertEquals(mockManagerDto.getId(), managerResponses.get(0).getId());
        assertEquals(mockManagerDto.getUserEmail(), managerResponses.get(0).getUser().getEmail());
    }

    @Test
    public void manager가_정상적으로_등록된다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

        long todoId = 1L;
        Todo todo = Todo.create("Test Title", "Test Contents", "Sunny", user);

        long managerUserId = 2L;
        User managerUser = User.create("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId); // request dto 생성

        given(todoService.getTodoById(todoId)).willReturn(todo);
        given(userService.getManagerUserById(managerUserId)).willReturn(managerUser);
        given(managerRepository.save(any(Manager.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ManagerSaveResponse response = managerService.saveManager(authUser, todoId, managerSaveRequest);

        // then
        assertNotNull(response);
        assertEquals(managerUser.getId(), response.getUser().getId());
        assertEquals(managerUser.getEmail(), response.getUser().getEmail());
    }

    @Test
    public void 일정_작성자가_아닌_유저가_매니저를_등록하면_InvalidRequestException_을_던진다() {
        // given
        AuthUser authUser = new AuthUser(1L, "asd@asd.com", UserRole.USER);
        long todoId = 1L;
        long managerUserId = 2L;

        User otherUser = User.create("qwer@qwer.com", "pass", UserRole.USER);

        Todo todo = Todo.create("title", "contents", "Sunny", otherUser);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);

        given(todoService.getTodoById(todoId)).willReturn(todo);

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.saveManager(authUser, todoId, managerSaveRequest)
        );

        assertEquals("일정을 생성한 유저만 담당자를 지정할 수 있습니다.", exception.getMessage());
    }

    @Test
    public void 일정_작성자가_본인을_매니저로_등록하면_InvalidRequestException_을_던진다() {
        // given
        AuthUser authUser = new AuthUser(1L, "asd@asd.com", UserRole.USER);
        long todoId = 1L;
        long managerUserId = 2L;

        User user = User.fromAuthUser(authUser);

        Todo todo = Todo.create("title", "contents", "Sunny", user);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);

        given(todoService.getTodoById(todoId)).willReturn(todo);
        given(userService.getManagerUserById(managerUserId)).willReturn(user);

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.saveManager(authUser, todoId, managerSaveRequest)
        );

        assertEquals("일정 작성자는 본인을 담당자로 등록할 수 없습니다.", exception.getMessage());
    }

    @Test
    public void Manager를_삭제할_수_있다() {
        // given
        long todoId = 1L;
        long managerId = 2L;

        AuthUser authUser = new AuthUser(1L, "asd@asd.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        Todo todo = Todo.create("title", "contents", "Sunny", user);

        Manager manager = Manager.create(user, todo);
        ReflectionTestUtils.setField(manager, "id", managerId);

        given(todoService.getTodoById(anyLong())).willReturn(todo);
        given(managerRepository.findById(anyLong())).willReturn(Optional.of(manager));
        doNothing().when(managerRepository).delete(manager);

        // when
        managerService.deleteManager(authUser, todoId, managerId);

        // then
        verify(managerRepository, times(1)).delete(manager);
    }

    @Test
    public void 일정_작성자가_아닌_유저가_매니저를_삭제하면_InvalidRequestException_을_던진다() {
        // given
        long todoId = 1L;
        long managerId = 2L;

        AuthUser authUser = new AuthUser(1L, "asd@asd.com", UserRole.USER);

        User otherUser = User.create("asdf@asdf.com", "pass", UserRole.USER);

        Todo todo = Todo.create("title", "contents", "Sunny", otherUser);

        given(todoService.getTodoById(todoId)).willReturn(todo);

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.deleteManager(authUser, todoId, managerId)
        );

        assertEquals("해당 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    void deleteManager_할_todo의_user가_null이면_예외를_던진다() {
        // given
        long todoId = 1L;
        long managerId = 2L;

        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);

        Todo todo = Todo.create("title", "contents", "Sunny", null);
        ReflectionTestUtils.setField(todo, "id", todoId);

        given(todoService.getTodoById(todoId)).willReturn(todo);

        // when & then
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> managerService.deleteManager(authUser, todoId, managerId)
        );

        assertEquals("해당 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    public void 해당_일정에_등록되지_않은_Manager를_삭제하면_InvalidRequestException_을_던진다() {
        // given
        long todoId = 1L;
        long managerId = 2L;

        AuthUser authUser = new AuthUser(1L, "asd@asd.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        Todo todo = Todo.create("title", "contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        Todo otherTodo = Todo.create("title", "contents", "Sunny", user);
        ReflectionTestUtils.setField(otherTodo, "id", 99L);

        Manager manager = Manager.create(user, otherTodo);
        ReflectionTestUtils.setField(manager, "id", managerId);

        given(todoService.getTodoById(todoId)).willReturn(todo);
        given(managerRepository.findById(managerId)).willReturn(Optional.of(manager));

        // when & then
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> managerService.deleteManager(authUser, todoId, managerId)
        );

        assertEquals("해당 일정에 등록된 담당자가 아닙니다.", exception.getMessage());
    }
}
