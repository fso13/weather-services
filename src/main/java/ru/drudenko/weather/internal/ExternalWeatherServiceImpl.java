package ru.drudenko.weather.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.drudenko.weather.domain.Weather;
import ru.drudenko.weather.services.ExternalWeatherService;

@Component("externalWeatherService")
public class ExternalWeatherServiceImpl implements ExternalWeatherService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String APPID = "b750eceec95423961c453569fe5f1726";
    private static final String HTTP_API_OPENWEATHERMAP_ORG_DATA_2_5_FORECAST = "http://api.openweathermap.org/data/2.5/forecast";
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Weather getWeatherByCity(String city) throws HttpClientErrorException {
        ResponseEntity<Result> result = restTemplate.getForEntity(HTTP_API_OPENWEATHERMAP_ORG_DATA_2_5_FORECAST + "?q={city}&APPID={APPID}", Result.class, city, APPID);
        log.info("Response openweathermap: {}", result);
        return convertResult(result);
    }

    @Override
    public Weather getWeatherByGeoCoordinates(double lon, double lat) throws HttpClientErrorException {
        ResponseEntity<Result> result = restTemplate.getForEntity(HTTP_API_OPENWEATHERMAP_ORG_DATA_2_5_FORECAST + "?lon={lon}&lat={lat}&APPID={APPID}", Result.class, lon, lat, APPID);
        return convertResult(result);
    }

    private Weather convertResult(ResponseEntity<Result> entity) {
        if (entity.getStatusCode().equals(HttpStatus.OK)) {
            Result result = entity.getBody();
            if (result == null || result.getList().isEmpty()) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
            }

            String city = result.getCity() == null ? "" : result.getCity().getName();
            List list = result.getList().get(0);
            return new Weather(city,
                    list.getMain().getTemp(),
                    list.getWind().getSpeed(),
                    list.getWind().getDeg());
        } else {
            throw new HttpClientErrorException(entity.getStatusCode(), entity.toString());
        }
    }
}
