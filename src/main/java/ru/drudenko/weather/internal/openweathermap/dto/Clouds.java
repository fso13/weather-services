package ru.drudenko.weather.internal.openweathermap.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Clouds {
    @SerializedName("all")
    @Expose
    private Integer all;
}
