package ru.drudenko.weather.controllers;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.drudenko.weather.TestConfig;
import ru.drudenko.weather.internal.openweathermap.dto.City;
import ru.drudenko.weather.internal.openweathermap.dto.List;
import ru.drudenko.weather.internal.openweathermap.dto.Main;
import ru.drudenko.weather.internal.openweathermap.dto.Result;
import ru.drudenko.weather.internal.openweathermap.dto.Wind;

import java.util.ArrayList;
import java.util.Base64;

@RunWith(SpringRunner.class)
@WebMvcTest(WeatherController.class)
@Import({TestConfig.class})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "ru.drudenko.weather", uriPort = 80)
public class WeatherControllerTest {
    public static final String CITY = "Москва";
    public static final String ENDPOINT_WEATHER_CITY = "/weather?city={name}";
    public static final String ENDPOINT_WEATHER_LON_LAT = "/weather?lon={lon}&lat={lat}";
    public static final String VALID_CREDENTIALS_HEADER = "Basic " + Base64.getEncoder().encodeToString("user1:1111".getBytes());
    public static final String NO_VALID_CREDENTIALS_HEADER = "Basic " + Base64.getEncoder().encodeToString("user1:fail".getBytes());
    private final ManualRestDocumentation restDocumentation = new ManualRestDocumentation();
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .alwaysDo(MockMvcRestDocumentation.document("{method-name}",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint())))
                .build();
    }

    @Test
    public void getWeatherByCityOk() throws Exception {

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT_WEATHER_CITY, CITY)
                        .accept("application/json")
                        .contentType("application/json")
                        .header("authorization", VALID_CREDENTIALS_HEADER))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        PayloadDocumentation.responseFields(PayloadDocumentation.fieldWithPath("city").description(
                                "Название города"), PayloadDocumentation.fieldWithPath("temperature").description(
                                "Температура"), PayloadDocumentation.fieldWithPath("wind").description(
                                "Направление ветра"))));

        // Then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("city", CoreMatchers.is(CITY)))
                .andExpect(MockMvcResultMatchers.jsonPath("temperature", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("wind", CoreMatchers.notNullValue()));
    }


    @Test
    public void getWeatherByCityBadRequest() throws Exception {

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT_WEATHER_CITY, "")
                        .accept("application/json")
                        .contentType("application/json")
                        .header("authorization", VALID_CREDENTIALS_HEADER))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        PayloadDocumentation.responseFields(PayloadDocumentation.fieldWithPath("code").description(
                                "Http code"), PayloadDocumentation.fieldWithPath("message").description(
                                "Error message"))));

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code", CoreMatchers.is(400)))
                .andExpect(MockMvcResultMatchers.jsonPath("message", CoreMatchers.is("City is mandatory!")));
    }

    @Test
    public void getWeatherByCityBadCredentials() throws Exception {

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT_WEATHER_CITY, CITY)
                        .accept("application/json")
                        .contentType("application/json")
                        .header("authorization", NO_VALID_CREDENTIALS_HEADER))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        PayloadDocumentation.responseFields(PayloadDocumentation.fieldWithPath("code").description(
                                "Http code"), PayloadDocumentation.fieldWithPath("message").description(
                                "Error message"))));

        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("code", CoreMatchers.is(401)))
                .andExpect(MockMvcResultMatchers.jsonPath("message", CoreMatchers.is("Bad credentials")));
    }

    @Test
    public void getWeatherByCoordinatesOk() throws Exception {

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT_WEATHER_LON_LAT, 20.0,  30.0)
                        .accept("application/json")
                        .contentType("application/json")
                        .header("authorization", VALID_CREDENTIALS_HEADER))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        PayloadDocumentation.responseFields(PayloadDocumentation.fieldWithPath("city").description(
                                "Название города"), PayloadDocumentation.fieldWithPath("temperature").description(
                                "Температура"), PayloadDocumentation.fieldWithPath("wind").description(
                                "Направление ветра"))));

        // Then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("city", CoreMatchers.is(CITY)))
                .andExpect(MockMvcResultMatchers.jsonPath("temperature", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("wind", CoreMatchers.notNullValue()));
    }

    @Test
    public void getWeatherByCoordinatesBadCredentials() throws Exception {

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT_WEATHER_LON_LAT, 20.0, 30.0)
                        .accept("application/json")
                        .contentType("application/json")
                        .header("authorization", NO_VALID_CREDENTIALS_HEADER))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        PayloadDocumentation.responseFields(PayloadDocumentation.fieldWithPath("code").description(
                                "Http code"), PayloadDocumentation.fieldWithPath("message").description(
                                "Error message"))));

        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("code", CoreMatchers.is(401)))
                .andExpect(MockMvcResultMatchers.jsonPath("message", CoreMatchers.is("Bad credentials")));
    }
    @Test
    public void contextLoads() {
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