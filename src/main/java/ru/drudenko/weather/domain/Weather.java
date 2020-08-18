package ru.drudenko.weather.domain;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Weather {
    private final String city;
    private final int temperature;
    private final String wind;

    public Weather(String city, int temperature, String wind) {
        this.city = city;
        this.temperature = temperature;
        this.wind = wind;
    }
}
