package ru.drudenko.weather.mapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import ru.drudenko.weather.domain.Weather;
import ru.drudenko.weather.internal.openweathermap.dto.List;
import ru.drudenko.weather.internal.openweathermap.dto.Result;

public class WeatherMapper {
    public static Weather convertResult(ResponseEntity<Result> entity) {
        if (entity.getStatusCode().equals(HttpStatus.OK)) {
            Result result = entity.getBody();
            if (result == null || result.getList().isEmpty()) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
            }

            String city = result.getCity() == null ? "" : result.getCity().getName();
            List list = result.getList().get(0);
            return new Weather(city,
                    list.getMain().getTemp(),
                    list.getWind().getSpeed(),
                    list.getWind().getDeg());
        } else {
            throw new HttpClientErrorException(entity.getStatusCode(), entity.toString());
        }
    }
}
