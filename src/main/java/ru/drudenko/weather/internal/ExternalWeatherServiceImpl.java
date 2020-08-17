package ru.drudenko.weather.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.drudenko.weather.domain.Weather;
import ru.drudenko.weather.internal.openweathermap.OpenWeatherMapClient;
import ru.drudenko.weather.internal.openweathermap.dto.Result;
import ru.drudenko.weather.mapper.WeatherMapper;
import ru.drudenko.weather.services.ExternalWeatherService;

@Service
public class ExternalWeatherServiceImpl implements ExternalWeatherService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final OpenWeatherMapClient openWeatherMapClient;

    @Autowired
    public ExternalWeatherServiceImpl(OpenWeatherMapClient openWeatherMapClient) {
        this.openWeatherMapClient = openWeatherMapClient;
    }

    @Cacheable("weather")
    @Override
    public Weather getWeatherByCity(String city) {
        log.info("Request openweathermap: city: {}", city);
        ResponseEntity<Result> result = openWeatherMapClient.getResult(city);
        log.info("Response openweathermap: {}", result);
        return WeatherMapper.convertResult(result);
    }

    @Cacheable("weather")
    @Override
    public Weather getWeatherByGeoCoordinates(double lon, double lat) {
        log.info("Request openweathermap: lon: {}, lat: {}", lon, lat);
        ResponseEntity<Result> result = openWeatherMapClient.getResult(lon, lat);
        log.info("Response openweathermap: {}", result);
        return WeatherMapper.convertResult(result);
    }
}
