package com.example.upstream.service;

import com.example.upstream.api.client.DownstreamFeignClient;
import com.example.upstream.service.contracts.CoreDownstreamResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoreService {

    private final DownstreamFeignClient downstreamFeignClient;

    public CoreDownstreamResponse getDownstreamResponse() {
        String downstreamResponse = null;
        try {
            downstreamResponse = downstreamFeignClient.fetchCoreResponse();
        } catch (Exception e){
            downstreamResponse = "Downstream service unhealthy";
        }
        return CoreDownstreamResponse.builder()
                .downstreamResponse(downstreamResponse)
                .upstreamResponse("I am not dependent on downstream service :)")
                .build();
    }

}
