package com.example.upstream.service.contracts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreDownstreamResponse {
    private String downstreamResponse;
    private String upstreamResponse;
}
