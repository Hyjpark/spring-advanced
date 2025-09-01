package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class FilterConfigTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void jwtFilterBean이_등록되고_url패턴이_설정된다() {
        // given
        FilterConfig filterConfig = new FilterConfig(jwtUtil, objectMapper);

        // when
        FilterRegistrationBean<JwtFilter> registrationBean = filterConfig.jwtFilter();

        // then
        assertNotNull(registrationBean);
        assertInstanceOf(JwtFilter.class, registrationBean.getFilter());
        assertThat(registrationBean.getUrlPatterns()).contains("/*");
    }
}
