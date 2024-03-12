package com.example.upstream.api.configuration;

import com.example.upstream.api.Target;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("circuit-breaker")
public class CircuitBreakerConfiguration {
    private Config downstreamConfig;
    private Config defaultConfig;

    @Data
    public static class Config {
        private int failureRateThreshold;
        private int waitDurationInOpenState;
        private int permittedNumberOfCallsInHalfOpenState;
        private int slidingWindowSize;
        private int slowCallRateThreshold;
        private int slowCallDurationThreshold;
        private int minimumNumberOfCalls;
    }

    public CircuitBreakerConfiguration.Config getConfig(Target target) {
        switch (target) {
            default:
                return defaultConfig;
        }
    }
}