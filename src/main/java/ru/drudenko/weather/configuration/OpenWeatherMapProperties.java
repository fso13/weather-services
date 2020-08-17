package ru.drudenko.weather.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "openweathermap")
public class OpenWeatherMapProperties {

    private Integer connectTimeout = 1000;
    private Integer connectResetTimeout = 1000;
    private Integer maxTotal = 20;
    private Integer defaultMaxPerRoute = 10;

    private String url;
    private String appid;
}
