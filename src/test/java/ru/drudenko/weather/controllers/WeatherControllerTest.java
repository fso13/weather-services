package ru.drudenko.weather.controllers;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.drudenko.weather.TestConfig;

import java.util.Base64;

@RunWith(SpringRunner.class)
@WebMvcTest(WeatherController.class)
@Import({TestConfig.class})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "https://weather-services.herokuapp.com/", uriPort = 443)
public class WeatherControllerTest {
    public static final String CITY = "Москва";
    public static final String ENDPOINT_WEATHER = "/weather";
    public static final String VALID_CREDENTIALS_HEADER = "Basic " + Base64.getEncoder().encodeToString("user1:1111".getBytes());
    public static final String NO_VALID_CREDENTIALS_HEADER = "Basic " + Base64.getEncoder().encodeToString("user1:fail".getBytes());
    ;
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

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT_WEATHER)
                        .param("city", CITY)
                        .accept("application/json")
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .header("authorization", VALID_CREDENTIALS_HEADER))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        RequestDocumentation.requestParameters(RequestDocumentation.parameterWithName("city").description(
                                        "Название города").attributes(Attributes.key("constraints").value("Обязательное"))),
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

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT_WEATHER)
                        .param("city", "")
                        .accept("application/json")
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .header("authorization", VALID_CREDENTIALS_HEADER))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        RequestDocumentation.requestParameters(RequestDocumentation.parameterWithName("city").description(
                                "Название города").attributes(Attributes.key("constraints").value("Обязательное"))),
                        PayloadDocumentation.responseFields(PayloadDocumentation.fieldWithPath("code").description(
                                "Http код"), PayloadDocumentation.fieldWithPath("message").description(
                                "Описание ошибки"))));

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code", CoreMatchers.is(400)))
                .andExpect(MockMvcResultMatchers.jsonPath("message", CoreMatchers.is("City is mandatory!")));
    }

    @Test
    public void getWeatherByCityBadCredentials() throws Exception {

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT_WEATHER)
                        .param("city", CITY)
                        .accept("application/json")
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .header("authorization", NO_VALID_CREDENTIALS_HEADER))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        RequestDocumentation.requestParameters(RequestDocumentation.parameterWithName("city").description(
                                "Название города").attributes(Attributes.key("constraints").value("Обязательное"))),
                        PayloadDocumentation.responseFields(PayloadDocumentation.fieldWithPath("code").description(
                                "Http код"), PayloadDocumentation.fieldWithPath("message").description(
                                "Описание ошибки"))));

        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("code", CoreMatchers.is(401)))
                .andExpect(MockMvcResultMatchers.jsonPath("message", CoreMatchers.is("Bad credentials")));
    }

    @Test
    public void getWeatherByCoordinatesOk() throws Exception {

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT_WEATHER)
                        .param("lon", "20.0")
                        .param("lat", "30.0")
                        .accept("application/json")
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .header("authorization", VALID_CREDENTIALS_HEADER))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        RequestDocumentation.requestParameters(RequestDocumentation.parameterWithName("lon").description(
                                "Долгота").attributes(Attributes.key("constraints").value("Обязательное")),
                                RequestDocumentation.parameterWithName("lat").description(
                                        "Широта").attributes(Attributes.key("constraints").value("Обязательное"))),
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

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT_WEATHER)
                        .param("lon", "20.0")
                        .param("lat", "30.0")
                        .accept("application/json")
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .header("authorization", NO_VALID_CREDENTIALS_HEADER))
                .andDo(MockMvcRestDocumentation.document("{method-name}",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        RequestDocumentation.requestParameters(RequestDocumentation.parameterWithName("lon").description(
                                        "Долгота").attributes(Attributes.key("constraints").value("Обязательное")),
                                RequestDocumentation.parameterWithName("lat").description(
                                        "Широта").attributes(Attributes.key("constraints").value("Обязательное"))),
                        PayloadDocumentation.responseFields(PayloadDocumentation.fieldWithPath("code").description(
                                "Http код"), PayloadDocumentation.fieldWithPath("message").description(
                                "Описание ошибки"))));

        resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("code", CoreMatchers.is(401)))
                .andExpect(MockMvcResultMatchers.jsonPath("message", CoreMatchers.is("Bad credentials")));
    }

    @Test
    public void contextLoads() {
    }
}