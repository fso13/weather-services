package ru.drudenko.weather.internal.openweathermap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.drudenko.weather.internal.openweathermap.dto.Result;

@Component
public class OpenWeatherMapClient {
    private final RestTemplate restTemplate;
    @Value("${openweathermap.url:http://api.openweathermap.org/data/2.5/forecast}")
    private String openweathermapUrl;
    @Value("${openweathermap.appid:b750eceec95423961c453569fe5f1726}")
    private String openweathermapAppid;

    @Autowired
    public OpenWeatherMapClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retryable(value = Exception.class, maxAttempts = 5)
    public ResponseEntity<Result> getResult(String city) {
        return restTemplate.getForEntity(openweathermapUrl + "?q={city}&APPID={APPID}", Result.class, city, openweathermapAppid);
    }

    public ResponseEntity<Result> getResult(double lon, double lat) {
        return restTemplate.getForEntity(openweathermapUrl + "?lon={lon}&lat={lat}&APPID={APPID}", Result.class, lon, lat, openweathermapAppid);
    }
}
