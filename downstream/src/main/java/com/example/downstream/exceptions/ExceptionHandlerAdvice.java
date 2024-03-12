package com.example.downstream.exceptions;


import com.example.downstream.model.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerAdvice {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<?> handleInvalidRequestException(final IllegalArgumentException e) {
        var genericResponse = GenericResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .error(e.getMessage())
                .build();
        return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
    }

}
