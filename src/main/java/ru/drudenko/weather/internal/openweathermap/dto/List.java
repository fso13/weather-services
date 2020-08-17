package ru.drudenko.weather.internal.openweathermap.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class List {
    @SerializedName("dt")
    @Expose
    private Integer dt;
    @SerializedName("main")
    @Expose
    private Main main;
    @SerializedName("clouds")
    @Expose
    private Clouds clouds;
    @SerializedName("wind")
    @Expose
    private Wind wind;
    @SerializedName("dt_txt")
    @Expose
    private String dtTxt;

    public List() {
    }

    public Integer getDt() {
        return dt;
    }

    public void setDt(Integer dt) {
        this.dt = dt;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public String getDtTxt() {
        return dtTxt;
    }

    public void setDtTxt(String dtTxt) {
        this.dtTxt = dtTxt;
    }

    @Override
    public String toString() {
        return "List{" +
                "dt=" + dt +
                ", main=" + main +
                ", clouds=" + clouds +
                ", wind=" + wind +
                ", dtTxt='" + dtTxt + '\'' +
                '}';
    }
}
