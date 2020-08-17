package ru.drudenko.weather.internal.openweathermap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.drudenko.weather.configuration.OpenWeatherMapProperties;
import ru.drudenko.weather.internal.openweathermap.dto.Result;

@Component
public class OpenWeatherMapClient {
    private final RestTemplate restTemplate;
    private final OpenWeatherMapProperties properties;

    @Autowired
    public OpenWeatherMapClient(RestTemplate restTemplate, OpenWeatherMapProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Retryable(value = Exception.class, maxAttempts = 5)
    public ResponseEntity<Result> getResult(String city) {
        return restTemplate.getForEntity(properties.getUrl() + "?q={city}&APPID={APPID}", Result.class, city, properties.getAppid());
    }

    public ResponseEntity<Result> getResult(double lon, double lat) {
        return restTemplate.getForEntity(properties.getUrl() + "?lon={lon}&lat={lat}&APPID={APPID}", Result.class, lon, lat, properties.getAppid());
    }
}
