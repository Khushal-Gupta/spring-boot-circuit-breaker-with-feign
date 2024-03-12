package com.example.upstream.api.configuration;

import com.example.upstream.api.Target;
import com.example.upstream.api.client.CustomErrorDecoder;
import feign.Feign;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import jakarta.annotation.PreDestroy;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

public class DownstreamFeignConfig {

    private OkHttpClient okHttpClient;

    @Bean
    public ConnectionPool downstreamConnectionPool(
            FeignHttpClientProperties httpClientProperties
    ) {
        return new ConnectionPool(
                httpClientProperties.getMaxConnections(),
                httpClientProperties.getTimeToLive(),
                TimeUnit.MILLISECONDS);
    }

    @Bean
    public feign.okhttp.OkHttpClient httpClient (
            @Qualifier("downstreamConnectionPool") ConnectionPool connectionPool,
            FeignHttpClientProperties httpClientProperties,
            FeignClientProperties feignClientProperties,
            PrometheusMeterRegistry meterRegistry
    ) {
        FeignClientProperties.FeignClientConfiguration clientConfiguration = feignClientProperties.getConfig()
                .get("downstream");

        this.okHttpClient = new OkHttpClient.Builder()
                .readTimeout((long)   clientConfiguration.getReadTimeout(), TimeUnit.MILLISECONDS)
                .connectTimeout((long) clientConfiguration.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .followRedirects(httpClientProperties.isFollowRedirects())
                .connectionPool(connectionPool)
                .eventListener(OkHttpMetricsEventListener.builder(meterRegistry, "okhttp.downstream").build())
                .build();
        return new feign.okhttp.OkHttpClient(this.okHttpClient);
    }

    @PreDestroy
    public void destroy() {
        if (this.okHttpClient != null) {
            this.okHttpClient.dispatcher().executorService().shutdown();
            this.okHttpClient.connectionPool().evictAll();
        }
    }

    @Bean(name = "downstreamFeignClient")
    public Feign.Builder feignBuilder(CircuitBreakerFactory circuitBreakerFactory, CustomErrorDecoder errorDecoder) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.getCircuitBreaker(Target.DOWNSTREAM);
        feign.okhttp.OkHttpClient okHttpClient1 = new feign.okhttp.OkHttpClient(okHttpClient);
        FeignDecorators decorators = FeignDecorators.builder()
                .withCircuitBreaker(circuitBreaker)
                .build();
        return Resilience4jFeign.builder(decorators).client(okHttpClient1).errorDecoder(errorDecoder);
    }
}
