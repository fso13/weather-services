package ru.drudenko.weather.services;

import ru.drudenko.weather.domain.Weather;

public interface ExternalWeatherService {

    Weather getWeatherByCity(String city);

    Weather getWeatherByGeoCoordinates(double lon, double lat);
}
