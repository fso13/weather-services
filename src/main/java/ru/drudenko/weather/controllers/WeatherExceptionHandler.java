package ru.drudenko.weather.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import ru.drudenko.weather.domain.exception.ApiException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@ControllerAdvice
@ResponseBody
public class WeatherExceptionHandler extends BasicAuthenticationEntryPoint {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiException> handleException(AuthenticationException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(401).body(new ApiException(HttpStatus.UNAUTHORIZED, e.getMessage()));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiException> handleException(ApiException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(e.getCode()).body(e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiException> handleException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ApiException(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiException> handleException(HttpClientErrorException e) {
        log.error("HttpClient exception", e);
        return ResponseEntity.status(e.getStatusCode()).body(new ApiException(e.getStatusCode(), e.getStatusText()));
    }

    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<ApiException> handleException(InterruptedException e) {
        log.error("Interrupted exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    @ExceptionHandler(ExecutionException.class)
    public ResponseEntity<ApiException> handleException(ExecutionException e) {
        log.error("Execution exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ApiException> handleException(TimeoutException e) {
        log.error("Timeout exception", e);
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new ApiException(HttpStatus.REQUEST_TIMEOUT, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiException> handleException(Exception e) {
        log.error("Unknown exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getOutputStream().println(objectMapper.writeValueAsString(new ApiException(HttpStatus.UNAUTHORIZED, authEx.getMessage())));
    }

    @Override
    public void afterPropertiesSet() {
        setRealmName("weather");
        super.afterPropertiesSet();
    }
}
