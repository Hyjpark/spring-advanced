package org.example.expert.domain.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerQueryDto;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManagerController.class)
public class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ManagerService managerService;

    @MockBean
    private AuthUserArgumentResolver authUserArgumentResolver;

    @Test
    public void Manager_저장_성공() throws Exception {
        // given
        long todoId = 1L;
        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(2L);
        ManagerSaveResponse managerSaveResponse = ManagerSaveResponse.of(1L, UserResponse.of(2L, "qwer@qwer.com"));

        given(managerService.saveManager(any(AuthUser.class), anyLong(), any(ManagerSaveRequest.class))).willReturn(managerSaveResponse);

        // when * then
        mockMvc.perform(post("/todos/{todoId}/managers", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerSaveRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void Manager_목록_조회_성공() throws Exception {
        // given
        long todoId = 1L;
        List<ManagerResponse> managerResponseList = List.of(
                ManagerResponse.of(new ManagerQueryDto(1L, 1L, "asd@asd.com", todoId)),
                ManagerResponse.of(new ManagerQueryDto(2L, 2L, "qwer@qwer.com", todoId))
        );
        given(managerService.getManagers(anyLong())).willReturn(managerResponseList);

        // when & then
        mockMvc.perform(get("/todos/{todoId}/managers", todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(managerResponseList.size()))
                .andExpect(jsonPath("$[0].id").value(managerResponseList.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(managerResponseList.get(1).getId()));
    }

}
