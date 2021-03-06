package ru.drudenko.weather.controllers;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;
import ru.drudenko.weather.internal.ExternalWeatherServiceImpl;
import ru.drudenko.weather.internal.openweathermap.OpenWeatherMapClient;
import ru.drudenko.weather.internal.openweathermap.dto.City;
import ru.drudenko.weather.internal.openweathermap.dto.List;
import ru.drudenko.weather.internal.openweathermap.dto.Main;
import ru.drudenko.weather.internal.openweathermap.dto.Result;
import ru.drudenko.weather.internal.openweathermap.dto.Wind;

import java.util.ArrayList;
import java.util.Base64;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeatherControllerTest {
    public static final String CITY = "Москва";
    public static final String ENDPOINT_WEATHER_CITY = "/weather?city={1}";
    public static final String ENDPOINT_WEATHER_LON_LAT = "/weather?lon={1}&lat={2}";
    public static final String VALID_CREDENTIALS_HEADER = "Basic " + Base64.getEncoder().encodeToString("user1:1111".getBytes());
    public static final String NO_VALID_CREDENTIALS_HEADER = "Basic " + Base64.getEncoder().encodeToString("user1:fail".getBytes());

    @LocalServerPort
    private int port;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private OpenWeatherMapClient openWeatherMapClient;
    @Autowired
    private ExternalWeatherServiceImpl externalWeatherService;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/";

        openWeatherMapClient = (OpenWeatherMapClient) context.getBean("openWeatherMapClient");
        openWeatherMapClient = Mockito.mock(OpenWeatherMapClient.class);
        ReflectionTestUtils.setField(externalWeatherService, "openWeatherMapClient", openWeatherMapClient);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getWeatherByCityOk() {
        Mockito.when(openWeatherMapClient.getResult(CITY)).thenReturn(ResponseEntity.ok(getStubResult()));

        RestAssured.given().header("authorization", VALID_CREDENTIALS_HEADER)
                .get(ENDPOINT_WEATHER_CITY, CITY)
                .then().assertThat()
                .statusCode(200)
                .body("city", Matchers.equalTo(CITY))
                .body("temperature", Matchers.notNullValue())
                .body("wind", Matchers.notNullValue());
    }

    @Test
    public void getWeatherByCityBadRequest() {

        RestAssured.given().header("authorization", VALID_CREDENTIALS_HEADER)
                .get(ENDPOINT_WEATHER_CITY, "")
                .then().assertThat()
                .statusCode(400)
                .body("code", Matchers.equalTo(400))
                .body("message",Matchers.equalTo("City is mandatory!"));
    }

    @Test
    public void getWeatherByCoordinatesOk() {
        Mockito.when(openWeatherMapClient.getResult(20.0, 30.0)).thenReturn(ResponseEntity.ok(getStubResult()));

        RestAssured.given().header("authorization", VALID_CREDENTIALS_HEADER)
                .get(ENDPOINT_WEATHER_LON_LAT, 20.0, 30.0)
                .then().assertThat()
                .statusCode(200)
                .body("city", Matchers.equalTo(CITY))
                .body("temperature", Matchers.notNullValue())
                .body("wind", Matchers.notNullValue());
    }

    @Test
    public void getWeatherBadCredentials() {
        RestAssured.given().header("authorization", NO_VALID_CREDENTIALS_HEADER)
                .get(ENDPOINT_WEATHER_LON_LAT, 20.0, 30.0)
                .then().assertThat()
                .statusCode(401)
                .body("code", Matchers.equalTo(401))
                .body("message", Matchers.equalTo("Bad credentials"));
    }

    private Result getStubResult() {
        Result result = new Result();
        City city = new City();
        city.setName(CITY);
        result.setCity(city);

        java.util.List<List> listResult = new ArrayList<>();
        List e = new List();
        Main main = new Main();
        main.setTemp(293.0);
        Wind wind = new Wind();
        wind.setSpeed(3.0);
        wind.setDeg(133.0);

        e.setMain(main);
        e.setWind(wind);
        listResult.add(e);

        result.setList(listResult);
        return result;
    }
}