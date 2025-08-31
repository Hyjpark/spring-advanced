package org.example.expert.domain.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    @MockBean
    private AuthUserArgumentResolver authUserArgumentResolver;

    @Test
    public void Todo_저장에_성공한다() throws Exception{
        // given
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");
        TodoSaveResponse todoSaveResponse =
                TodoSaveResponse.of(
                        1L,
                        "title",
                        "contents",
                        "Sunny",
                        UserResponse.of(1L, "asd@asd.com"));

        given(todoService.saveTodo(any(AuthUser.class), any(TodoSaveRequest.class))).willReturn(todoSaveResponse);

        // when * then
        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoSaveRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void Todo_목록을_페이지_조회_성공() throws Exception {
        // given
        int page = 1;
        int size = 10;

        UserResponse userResponse = UserResponse.of(1L, "asd@asd.com");

        TodoResponse todo1 = TodoResponse.of(1L, "title1", "contents1", "Sunny", userResponse, LocalDateTime.now(), LocalDateTime.now());
        TodoResponse todo2 = TodoResponse.of(2L, "title2", "contents2", "Rainy", userResponse, LocalDateTime.now(), LocalDateTime.now());

        List<TodoResponse> todoList = List.of(todo1, todo2);
        PageImpl<TodoResponse> responsePage = new PageImpl<>(todoList);

        given(todoService.getTodos(page, size)).willReturn(responsePage);

        // when * then
        mockMvc.perform(get("/todos")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(todoList.size()))
                .andExpect(jsonPath("$.content[0].id").value(todo1.getId()))
                .andExpect(jsonPath("$.content[0].title").value(todo1.getTitle()))
                .andExpect(jsonPath("$.content[1].id").value(todo2.getId()))
                .andExpect(jsonPath("$.content[1].title").value(todo2.getTitle()));
    }
}
