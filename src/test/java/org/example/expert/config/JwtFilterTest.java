package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private JwtFilter jwtFilter;

    @Test
    void doFilter_정상토큰이면_chain실행하고_request속성설정() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        request.addHeader("Authorization", "Bearer token");
        request.setRequestURI("/users");

        Claims claims = mock(Claims.class);
        given(jwtUtil.substringToken("Bearer token")).willReturn("token");
        given(jwtUtil.extractClaims("token")).willReturn(claims);
        given(claims.get("userRole", String.class)).willReturn("USER");
        // httpRequest 속성
        given(claims.getSubject()).willReturn("1");
        given(claims.get("email")).willReturn("asd@asd.com");
        given(claims.get("userRole")).willReturn("USER");

        // when
        jwtFilter.doFilter(request, response, chain);

        // then
        verify(chain).doFilter(request, response);
        assertEquals(1L, request.getAttribute("userId"));
        assertEquals("asd@asd.com", request.getAttribute("email"));
        assertEquals("USER", request.getAttribute("userRole"));
    }
}
