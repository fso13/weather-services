package ru.drudenko.weather.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import ru.drudenko.weather.domain.Weather;
import ru.drudenko.weather.services.ExternalWeatherService;

import java.security.Principal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final long WAIT_WEATHER_IN_SECONDS = 30;

    private final ExternalWeatherService externalWeatherService;
    private final ThreadPoolExecutor executorService;

    @Autowired
    public WeatherController(ExternalWeatherService externalWeatherService) {
        this.executorService = new ThreadPoolExecutor(1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue(1000));
        this.externalWeatherService = externalWeatherService;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"city"})
    @PreAuthorize("hasAuthority('ADMIN_USER') or hasAuthority('USER')")
    public ResponseEntity weather(@RequestParam(value = "city") String city, @AuthenticationPrincipal Principal user) {
        log.debug("User: {}, get weather with params: {}", user.getName(), city);
        ResponseEntity entity;
        if (city == null || city.isEmpty()) {
            entity = ResponseEntity.badRequest().body("City is mandatory!");
        } else {
            try {
                log.info("Active task: {}, in queue: {}", executorService.getActiveCount(), executorService.getTaskCount());
                entity = executorService.submit(() -> {
                    try {
                        Weather weather = externalWeatherService.getWeatherByCity(city);
                        return ResponseEntity.ok(weather);
                    } catch (HttpClientErrorException e) {
                        return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
                    }
                }).get(WAIT_WEATHER_IN_SECONDS, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Interrupted exception", e);
                entity = ResponseEntity.status(500).body(e.getMessage());
            } catch (ExecutionException e) {
                log.error("Execution exception", e);
                entity = ResponseEntity.status(500).body(e.getMessage());
            } catch (TimeoutException e) {
                log.warn("Timeout exception", e);
                entity = ResponseEntity.status(408).body(e.getMessage());
            } catch (RuntimeException e) {
                log.error("Unknown exception", e);
                entity = ResponseEntity.status(500).body(e.getMessage());
            }
        }
        log.debug("User: {}, got response {}", user.getName(), entity);
        return entity;

    }

    @RequestMapping(method = RequestMethod.GET, params = {"lon", "lat"})
    @PreAuthorize("hasAuthority('ADMIN_USER') or hasAuthority('USER')")
    public ResponseEntity weather(@RequestParam(value = "lon") double lon, @RequestParam(value = "lat") double lat, @AuthenticationPrincipal Principal user) {
        log.debug("User: {}, get weather with params: lon={},lat={}", user.getName(), lon, lat);
        ResponseEntity entity;
        try {
            log.info("Active task: {}, in queue: {}", executorService.getActiveCount(), executorService.getTaskCount());
            entity = executorService.submit(() -> {
                try {
                    Weather weather = externalWeatherService.getWeatherByGeoCoordinates(lon, lat);
                    return ResponseEntity.ok(weather);
                } catch (HttpClientErrorException e) {
                    return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
                }
            }).get(WAIT_WEATHER_IN_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Interrupted exception", e);
            entity = ResponseEntity.status(500).body(e.getMessage());
        } catch (ExecutionException e) {
            log.error("Execution exception", e);
            entity = ResponseEntity.status(500).body(e.getMessage());
        } catch (TimeoutException e) {
            log.warn("Timeout exception", e);
            entity = ResponseEntity.status(408).body(e.getMessage());
        } catch (RuntimeException e) {
            log.error("Unknown exception", e);
            entity = ResponseEntity.status(500).body(e.getMessage());
        }
        log.debug("User: {}, got response {}", user.getName(), entity);
        return entity;
    }
}
