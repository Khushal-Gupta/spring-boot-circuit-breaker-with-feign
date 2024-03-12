package com.example.upstream.api.configuration;

import com.example.upstream.api.Target;
import com.example.upstream.exceptions.InvalidRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class CircuitBreakerFactory {

    private final Map<Target, CircuitBreaker> circuitBreakers = new HashMap<>();
    private final ObjectMapper objectMapper;
    private final CircuitBreakerConfiguration circuitBreakerConfiguration;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @PostConstruct
    public void postConstruct() {
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
        for (Target target : Target.values()) {
            CircuitBreakerConfiguration.Config config = circuitBreakerConfiguration.getConfig(target);
            circuitBreakers.put(target, createCircuitBreaker(target, config));
        }
    }

    public CircuitBreaker getCircuitBreaker(Target target) {
        if (circuitBreakers.containsKey(target)) {
            return circuitBreakers.get(target);
        }
        return circuitBreakers.get(Target.DEFAULT);
    }

    @SneakyThrows
    private CircuitBreaker createCircuitBreaker(Target target, CircuitBreakerConfiguration.Config config) {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(config.getFailureRateThreshold())
                .waitDurationInOpenState(Duration.ofMillis(config.getWaitDurationInOpenState()))
                .permittedNumberOfCallsInHalfOpenState(config.getPermittedNumberOfCallsInHalfOpenState())
                .slidingWindowSize(config.getSlidingWindowSize())
                .slowCallRateThreshold(config.getSlowCallRateThreshold())
                .slowCallDurationThreshold(Duration.ofMillis(config.getSlowCallDurationThreshold()))
                .minimumNumberOfCalls(config.getMinimumNumberOfCalls())
                .ignoreExceptions(InvalidRequestException.class)
                .build();
        return circuitBreakerRegistry.circuitBreaker(target.toString(), circuitBreakerConfig);
    }
}
