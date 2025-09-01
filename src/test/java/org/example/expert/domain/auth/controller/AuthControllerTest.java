package org.example.expert.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    public void 회원가입_성공() throws Exception {
        // given
        SignupRequest signupRequest = new SignupRequest("asd@asd.com", "pass", "USER");
        SignupResponse signupResponse = SignupResponse.of("mockedToken");

        given(authService.signup(any(SignupRequest.class))).willReturn(signupResponse);

        // when * then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void 로그인_성공() throws Exception {
        // given
        SigninRequest signinRequest = new SigninRequest("asd@asd.com", "pass");
        SigninResponse signinResponse = SigninResponse.of("mockedToken");

        given(authService.signin(any(SigninRequest.class))).willReturn(signinResponse);

        // when * then
        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signinRequest)))
                .andExpect(status().isOk());
    }
}
