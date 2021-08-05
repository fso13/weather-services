package ru.drudenko.weather;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import ru.drudenko.weather.configuration.SecurityConfig;
import ru.drudenko.weather.configuration.WebConfig;
import ru.drudenko.weather.controllers.WeatherControllerTest;
import ru.drudenko.weather.domain.Weather;
import ru.drudenko.weather.services.ExternalWeatherService;

@Import({SecurityConfig.class, WebConfig.class})
@TestConfiguration
public class TestConfig {

    @Bean
    public ExternalWeatherService externalWeatherService(){
        return new ExternalWeatherService() {
            @Override
            public Weather getWeatherByCity(String city) {
                return new Weather(WeatherControllerTest.CITY, 10, "NE");
            }

            @Override
            public Weather getWeatherByGeoCoordinates(double lon, double lat) {
                return new Weather(WeatherControllerTest.CITY, 10, "NE");
            }
        };
    }

}