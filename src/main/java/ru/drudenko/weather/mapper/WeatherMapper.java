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
                    list.getMain().getTemp().intValue() - 273,
                    list.getWind().getSpeed() + convertDeg(list.getWind().getDeg()));
        } else {
            throw new HttpClientErrorException(entity.getStatusCode(), entity.toString());
        }
    }

    private static String convertDeg(Double deg) {
        if (deg > 22.5 && deg <= 67.5) {
            return "NE";
        } else if (deg > 67.5 && deg <= 112.5) {
            return "E";
        } else if (deg > 112.5 && deg <= 157.5) {
            return "SE";
        } else if (deg > 157.5 && deg <= 202.5) {
            return "S";
        } else if (deg > 202.5 && deg <= 247.5) {
            return "SW";
        } else if (deg > 247.5 && deg <= 292.5) {
            return "W";
        } else if (deg > 292.5 && deg <= 337.5) {
            return "NW";
        } else {
            return "N";
        }
    }
}
