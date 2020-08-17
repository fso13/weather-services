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
import ru.drudenko.weather.domain.Weather;
import ru.drudenko.weather.services.ExternalWeatherService;

import java.security.Principal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ExternalWeatherService externalWeatherService;

    @Autowired
    public WeatherController(ExternalWeatherService externalWeatherService) {
        this.externalWeatherService = externalWeatherService;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"city"})
    @PreAuthorize("hasAuthority('ADMIN_USER') or hasAuthority('USER')")
    public Weather weather(@RequestParam(value = "city") String city, @AuthenticationPrincipal Principal user) throws InterruptedException, ExecutionException, TimeoutException {
        log.debug("User: {}, get weather with params: {}", user.getName(), city);
        if (city == null || city.isEmpty()) {
            throw new IllegalArgumentException("City is mandatory!");
        } else {
            Weather weather = externalWeatherService.getWeatherByCity(city);

            log.debug("User: {}, got response {}", user.getName(), weather);
            return weather;
        }

    }

    @RequestMapping(method = RequestMethod.GET, params = {"lon", "lat"})
    @PreAuthorize("hasAuthority('ADMIN_USER') or hasAuthority('USER')")
    public Weather weather(@RequestParam(value = "lon") double lon, @RequestParam(value = "lat") double lat, @AuthenticationPrincipal Principal user) throws InterruptedException, ExecutionException, TimeoutException {
        log.debug("User: {}, get weather with params: lon={},lat={}", user.getName(), lon, lat);
        Weather weather = externalWeatherService.getWeatherByGeoCoordinates(lon, lat);

        log.debug("User: {}, got response {}", user.getName(), weather);
        return weather;
    }
}
