package org.example.expert.common.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Aspect
@Component
@RequiredArgsConstructor
public class AdminLoggingAspect {

    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(AdminLoggingAspect.class);

    @Around("execution(* org.example.expert..controller.*AdminController.*(..))")
    public Object logMethodExecution(ProceedingJoinPoint pjp) throws Throwable {
        // 요청한 사용자의 ID
        // API 요청 시각
        // API 요청 URL
        // 요청 본문(RequestBody)
        // 응답 본문(ResponseBody)

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String methodName = pjp.getSignature().getName();

        Long userId = (Long) request.getAttribute("userId");
        String requestURI = request.getMethod() + "  " + request.getRequestURI();
        String requestBody = getRequestBody(pjp);

        log.info("===Admin API Logging===");
        log.info("Request 정보:  \nuserId: {}, \ntime: {}, \nURI: {}, \nMethod: {},\nRequestBody: {}",
                userId,
                LocalDateTime.now(),
                requestURI,
                methodName,
                requestBody
        );

        Object response;

        try {
            response = pjp.proceed();
        } catch (Exception e) {
            log.error("Exception: Method: {}, URI: {} message: {}",
                    methodName,
                    requestURI,
                    e.getMessage()
            );
            throw e;
        }

        String responseString = convertObjectToJson(response);
        String responseBody = extractBodyFormJson(responseString);

        log.info("Response 정보: \nuserId: {}, \ntime: {}, \nURI: {}, \nMethod: {},\nResponseBody: {}",
                userId,
                LocalDateTime.now(),
                requestURI,
                methodName,
                responseBody
        );

        return response;
    }

    private String getRequestBody(ProceedingJoinPoint pjp) {
        try {
            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            Object[] args = pjp.getArgs();

            return IntStream.range(0, parameterAnnotations.length)
                    .filter(i -> Arrays.stream(parameterAnnotations[i])
                            .anyMatch(a -> a.annotationType() == RequestBody.class))
                    .mapToObj(i -> convertObjectToJson(args[i]))
                    .collect(Collectors.joining(", "));
        } catch (Exception e) {
            log.error("Error serializing request body", e);
            return "";
        }
    }

    private String convertObjectToJson(Object object) {
        if (object == null) return "";

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error serializing request body", e);
            return "Error serializing object to JSON";
        }
    }

    private String extractBodyFormJson(String json) {
        if (json == null || json.isBlank()) return "";

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode body = root.path("body");
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
        } catch (JsonProcessingException e) {
            log.error("Error extracting body from JSON", e);
            return "Error extracting body from JSON";
        }
    }
}
