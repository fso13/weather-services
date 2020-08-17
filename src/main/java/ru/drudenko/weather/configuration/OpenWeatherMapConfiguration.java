package ru.drudenko.weather.configuration;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class OpenWeatherMapConfiguration {

    private final OpenWeatherMapProperties properties;

    @Autowired
    public OpenWeatherMapConfiguration(OpenWeatherMapProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(requestFactory());
    }

    private HttpComponentsClientHttpRequestFactory requestFactory() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setHttpClient(httpClient());
        httpRequestFactory.setConnectTimeout(properties.getConnectTimeout());
        httpRequestFactory.setConnectionRequestTimeout(properties.getConnectResetTimeout());
        return httpRequestFactory;
    }

    private HttpClient httpClient() {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(properties.getMaxTotal());
        connManager.setDefaultMaxPerRoute(properties.getDefaultMaxPerRoute());
        return HttpClientBuilder.create()
                .setConnectionManager(connManager)
                .build();
    }
}
