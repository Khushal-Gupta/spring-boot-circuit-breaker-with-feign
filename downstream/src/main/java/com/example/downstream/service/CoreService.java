package com.example.downstream.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CoreService {
    private String SUCCESS_KEY = "success";
    private String FAILURE_KEY = "failure";
    private String SLOW_KEY = "slow";

    private Map<String, Double> responseWeight = Map.ofEntries(
        Map.entry(SLOW_KEY, 0.1),
        Map.entry(SUCCESS_KEY, 0.8),
        Map.entry(FAILURE_KEY, 0.1)
    );

    private Integer slowResponseTimeInMilli = 5000;
    private final Random random = new Random();


    public Object getResponse() {
        double randomValue = random.nextDouble();
        double cumulativeWeight = 0.0;

        for (Map.Entry<String, Double> entry : responseWeight.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue <= cumulativeWeight) {
                return getResponseByType(entry.getKey());
            }
        }

        // Default to some response if weights are not properly defined
        return getSuccessResponse();
    }

    private Object getResponseByType(String responseType) {
        switch (responseType) {
            case "slow":
                return getSlowResponse();
            case "success":
                return getSuccessResponse();
            case "failure":
                return getFailureResponse();
            default:
                throw new IllegalArgumentException("Unknown response type: " + responseType);
        }
    }

    private Object getSlowResponse() {
        try {
            Thread.sleep(slowResponseTimeInMilli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return String.format("Delayed response: in %s ms", slowResponseTimeInMilli);
    }

    private String getSuccessResponse() {
        return "Success response";
    }

    private String getFailureResponse() {
        throw new RuntimeException("Failure response");
    }

    public Object setResponseWeight(Map<String, Double> responseWeight) {
        validateResponseWeight(responseWeight);
        this.responseWeight = responseWeight;
        return responseWeight;
    }

    private void validateResponseWeight(Map<String, Double> responseWeight) {
        double sum = 0.0;
        if(responseWeight.entrySet().size() != 3) {
            throw new IllegalArgumentException("Response weight should contain all of success, failure and slow keys");
        }
        for (Map.Entry<String, Double> entry : responseWeight.entrySet()) {
            sum += entry.getValue();
        }
        if (sum != 1.0) {
            throw new IllegalArgumentException("Sum of response weights should be 1.0");
        }
        if(!responseWeight.containsKey(SUCCESS_KEY) || !responseWeight.containsKey(FAILURE_KEY) || !responseWeight.containsKey(SLOW_KEY)) {
            throw new IllegalArgumentException("Response weight should contain all of success, failure and slow keys");
        }
    }

    public String setSlowResponseTime(Map<String, Integer> map) {
        int slowResponseTimeInMilli = map.get("slowResponseTime");
        this.slowResponseTimeInMilli = slowResponseTimeInMilli;
        return "Slow response time updated :" + slowResponseTimeInMilli;
    }

}
