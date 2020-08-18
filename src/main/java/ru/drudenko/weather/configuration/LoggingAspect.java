package ru.drudenko.weather.configuration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.drudenko.weather.internal.ExternalWeatherServiceImpl;

@Aspect
@Component
public class LoggingAspect {
    private final Logger externalWeatherServiceLogger = LoggerFactory.getLogger(ExternalWeatherServiceImpl.class);

    @Pointcut("execution(public * ru.drudenko.weather.internal.ExternalWeatherServiceImpl.*(..))")
    private void annotatedByExternalWeatherServiceImpl() {
    }

    @Around("annotatedByExternalWeatherServiceImpl()")
    public Object errorHandlerJmsListenersWithReturnType(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            externalWeatherServiceLogger.info("Request: method:{}, args: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
            Object result = joinPoint.proceed();
            externalWeatherServiceLogger.info("Response: {}", result);
            return result;

        } catch (Exception ex) {
            externalWeatherServiceLogger.error(ex.getMessage(), ex);
            throw ex;
        }
    }
}
