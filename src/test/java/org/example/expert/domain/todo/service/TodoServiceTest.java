package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private TodoService todoService;

    @Test
    public void todo가_정상적으로_등록된다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        String weather = "Sunny";

        Todo todo = Todo.create("title", "contents", weather, user);

        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");

        given(weatherClient.getTodayWeather()).willReturn(weather);
        given(todoRepository.save(any(Todo.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        TodoSaveResponse todoSaveResponse = todoService.saveTodo(authUser, todoSaveRequest);

        // then
        assertNotNull(todoSaveResponse);
        assertEquals(todoSaveRequest.getTitle(), todoSaveResponse.getTitle());
        assertEquals(todoSaveRequest.getContents(), todoSaveResponse.getContents());
        assertEquals(weather, todoSaveResponse.getWeather());
        assertEquals(user.getId(), todoSaveResponse.getUser().getId());
        assertEquals(user.getEmail(), todoSaveResponse.getUser().getEmail());
    }

    @Test
    public void 존재하는_Todos를_조회하면_Page로_반환된다() {
        // given
        int page = 1;
        int size = 10;

        User user = User.create("asd@asd.com", "pass", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        Todo todo1 = Todo.create("title1", "contents1", "Sunny", user);
        ReflectionTestUtils.setField(todo1, "id", 1L);

        Todo todo2 = Todo.create("title2", "contents2", "Sunny", user);
        ReflectionTestUtils.setField(todo2, "id", 2L);

        List<Todo> todos = List.of(todo1, todo2);
        Pageable pageable = PageRequest.of(page - 1, size);
        PageImpl<Todo> todoPage = new PageImpl<>(todos, pageable, todos.size());

        given(todoRepository.findAllByOrderByModifiedAtDesc(any(Pageable.class))).willReturn(todoPage);

        // when
        Page<TodoResponse> todoResponses = todoService.getTodos(page, size);

        // then
        assertNotNull(todoResponses);
        assertThat(todoResponses.getTotalElements()).isEqualTo(todoPage.getTotalElements());

        List<TodoResponse> expectedResponses = List.of(
                TodoResponse.of(
                        todo1.getId(),
                        todo1.getTitle(),
                        todo1.getContents(),
                        todo1.getWeather(),
                        UserResponse.of(user.getId(), user.getEmail()),
                        todo1.getCreatedAt(),
                        todo1.getModifiedAt()
                ),
                TodoResponse.of(
                        todo2.getId(),
                        todo2.getTitle(),
                        todo2.getContents(),
                        todo2.getWeather(),
                        UserResponse.of(user.getId(), user.getEmail()),
                        todo2.getCreatedAt(),
                        todo2.getModifiedAt()
                )
        );

        // 전체 리스트를 한 번에 비교
        assertThat(todoResponses.getContent())
                .usingRecursiveComparison()
                .isEqualTo(expectedResponses);
    }


    @Test
    public void 존재하는_todoId로_조회하면_TodoResponse를_반환한다() {
        // given
        long todoId = 1L;

        User user = User.create("asd@asd.com", "pass", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        Todo todo = Todo.create("title", "contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", todoId);
        ReflectionTestUtils.setField(todo, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(todo, "modifiedAt", LocalDateTime.now());

        given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.of(todo));

        // when
        TodoResponse todoResponse = todoService.getTodo(todoId);

        // then
        assertNotNull(todoResponse);
        assertEquals(todo.getId(), todoResponse.getId());
        assertEquals(todo.getTitle(), todoResponse.getTitle());
        assertEquals(todo.getContents(), todoResponse.getContents());
        assertEquals(todo.getWeather(), todoResponse.getWeather());
        assertEquals(user.getId(), todoResponse.getUser().getId());
        assertEquals(user.getEmail(), todoResponse.getUser().getEmail());
        assertNotNull(todoResponse.getCreatedAt());
        assertNotNull(todoResponse.getModifiedAt());
    }

    @Test
    public void 존재하는_todoId로_조회하면_Todo를_반환한다() {
        // given
        long todoId = 1L;
        User user = User.create("asd@asd.com", "pass", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        Todo todo = Todo.create("title", "contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", todoId);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

        // when
        Todo response = todoService.getTodoById(todoId);

        // then
        assertNotNull(response);
        assertEquals(response.getId(), todoId);
        assertEquals(response.getTitle(), todo.getTitle());
        assertEquals(response.getContents(), todo.getContents());
        assertEquals(response.getWeather(), todo.getWeather());
        assertEquals(response.getUser().getId(), user.getId());
    }

    @Test
    public void 존재하지_않는_todoId로_조회하면_InvalidRequestException을_던진다() {
        // given
        long todoId = 1L;
        given(todoRepository.findById(todoId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> todoService.getTodoById(todoId));

        assertEquals("Todo not found", exception.getMessage());
    }
}
