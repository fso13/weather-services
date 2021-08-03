package ru.drudenko.weather.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver;

import java.util.List;
//BUG https://github.com/spring-projects/spring-framework/issues/26380
@Configuration
public class WebConfig extends DelegatingWebMvcConfiguration {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PrincipalMethodArgumentResolver());
    }
}
