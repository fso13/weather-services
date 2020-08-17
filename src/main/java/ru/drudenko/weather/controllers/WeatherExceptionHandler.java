package ru.drudenko.weather.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@ControllerAdvice
@ResponseBody
public class WeatherExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleException(HttpClientErrorException e) {
        log.error("HttpClient exception", e);
        return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
    }

    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<Object> handleException(InterruptedException e) {
        log.error("Interrupted exception", e);
        return ResponseEntity.status(500).body(e.getMessage());
    }

    @ExceptionHandler(ExecutionException.class)
    public ResponseEntity<Object> handleException(ExecutionException e) {
        log.error("Execution exception", e);
        return ResponseEntity.status(500).body(e.getMessage());
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<Object> handleException(TimeoutException e) {
        log.error("Timeout exception", e);
        return ResponseEntity.status(408).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        log.error("Unknown exception", e);
        return ResponseEntity.status(500).body(e.getMessage());
    }
}
