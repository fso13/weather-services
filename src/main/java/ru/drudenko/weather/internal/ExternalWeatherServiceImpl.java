package ru.drudenko.weather.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.drudenko.weather.domain.Weather;
import ru.drudenko.weather.internal.openweathermap.OpenWeatherMapClient;
import ru.drudenko.weather.mapper.WeatherMapper;
import ru.drudenko.weather.services.ExternalWeatherService;

@Service
public class ExternalWeatherServiceImpl implements ExternalWeatherService {
    private final OpenWeatherMapClient openWeatherMapClient;

    @Autowired
    public ExternalWeatherServiceImpl(OpenWeatherMapClient openWeatherMapClient) {
        this.openWeatherMapClient = openWeatherMapClient;
    }

    @Cacheable("weather")
    @Override
    public Weather getWeatherByCity(String city) {
        return WeatherMapper.convertResult(openWeatherMapClient.getResult(city));
    }

    @Cacheable("weather")
    @Override
    public Weather getWeatherByGeoCoordinates(double lon, double lat) {
        return WeatherMapper.convertResult(openWeatherMapClient.getResult(lon, lat));
    }
}
