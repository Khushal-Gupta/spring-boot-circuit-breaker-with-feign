package com.example.upstream.api.client;

import com.example.upstream.api.model.GenericResponse;
import com.example.upstream.exceptions.InvalidRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;

    private String GENERIC_ERROR_MESSAGE = "An error occurred while processing the request";

    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException exception = FeignException.errorStatus(methodKey, response);
        HttpStatus status = HttpStatus.valueOf(response.status());
        if (status.is4xxClientError()) {
            return new InvalidRequestException(getErrorMessage(exception));
        }
        return exception;
    }

    private String getErrorMessage(FeignException exception) {
        try {
            GenericResponse response = objectMapper.readValue(exception.contentUTF8(), GenericResponse.class);
            return response.getError();
        } catch (IOException ex) {
        }
        return GENERIC_ERROR_MESSAGE;
    }

}