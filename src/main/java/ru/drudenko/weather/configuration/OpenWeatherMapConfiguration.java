package ru.drudenko.weather.configuration;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class OpenWeatherMapConfiguration {
    @Value("${connect_timeout:1000}")
    private Integer connectTimeout;
    @Value("${connect_request_timeout:1000}")
    private Integer connectResetTimeout;
    @Value("${max_pool_size:20}")
    private Integer maxTotal;
    @Value("${max_per_route_size:10}")
    private Integer defaultMaxPerRoute;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(requestFactory());
    }

    private HttpComponentsClientHttpRequestFactory requestFactory() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setHttpClient(httpClient());
        httpRequestFactory.setConnectTimeout(connectTimeout);
        httpRequestFactory.setConnectionRequestTimeout(connectResetTimeout);
        return httpRequestFactory;
    }

    private HttpClient httpClient() {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(maxTotal);
        connManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return HttpClientBuilder.create()
                .setConnectionManager(connManager)
                .build();
    }
}
