package com.example.upstream.api.client;

import com.example.upstream.api.configuration.DownstreamFeignConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "downstream", url = "${downstream.service.base.url}", configuration = DownstreamFeignConfig.class)
@CircuitBreaker(name = "DOWNSTREAM")
public interface DownstreamFeignClient {

    @GetMapping(value = "/core")
    String fetchCoreResponse();
}
