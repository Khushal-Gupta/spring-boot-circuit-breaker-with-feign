package com.example.downstream.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class GenericResponse<T> {
    private int statusCode;
    private T data;
    private String message;
    private String error;
}