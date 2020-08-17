package ru.drudenko.weather.services;

import org.springframework.web.client.HttpClientErrorException;
import ru.drudenko.weather.domain.Weather;

public interface ExternalWeatherService {

    Weather getWeatherByCity(String city) throws HttpClientErrorException, InterruptedException;

    Weather getWeatherByGeoCoordinates(double lon, double lat) throws HttpClientErrorException, InterruptedException;
}
