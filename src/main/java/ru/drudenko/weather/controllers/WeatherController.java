package ru.drudenko.weather.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.drudenko.weather.domain.Weather;
import ru.drudenko.weather.services.ExternalWeatherService;

import java.security.Principal;

@Tag(name = "Weather controller")
@RestController
@RequestMapping("/weather")
public class WeatherController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ExternalWeatherService externalWeatherService;

    @Autowired
    public WeatherController(ExternalWeatherService externalWeatherService) {
        this.externalWeatherService = externalWeatherService;
    }

    @Operation(summary = "Get a weather by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the weather",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Weather.class))}),
            @ApiResponse(responseCode = "400", description = "City is mandatory!",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Bad credentials",
                    content = @Content)})
    @RequestMapping(method = RequestMethod.GET, params = {"city"})
    @PreAuthorize("hasAuthority('ADMIN_USER') or hasAuthority('USER')")
    public Weather weatherByCityName(@RequestParam(value = "city") String city,
                           @AuthenticationPrincipal Principal user) {

        log.debug("User: {}, get weather with params: {}", user.getName(), city);
        if (city == null || city.isEmpty()) {
            throw new IllegalArgumentException("City is mandatory!");
        } else {
            Weather weather = externalWeatherService.getWeatherByCity(city);

            log.debug("User: {}, got response {}", user.getName(), weather);
            return weather;
        }
    }

    @Operation(summary = "Get a weather by its lon and lat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the weather",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Weather.class))}),
            @ApiResponse(responseCode = "400", description = "City is mandatory!",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Bad credentials",
                    content = @Content)})
    @RequestMapping(method = RequestMethod.GET, params = {"lon", "lat"})
    @PreAuthorize("hasAuthority('ADMIN_USER') or hasAuthority('USER')")
    public Weather weatherByCoordinates(@RequestParam(value = "lon") double lon,
                           @RequestParam(value = "lat") double lat,
                           @AuthenticationPrincipal Principal user) {

        log.debug("User: {}, get weather with params: lon={},lat={}", user.getName(), lon, lat);
        Weather weather = externalWeatherService.getWeatherByGeoCoordinates(lon, lat);

        log.debug("User: {}, got response {}", user.getName(), weather);
        return weather;
    }
}
