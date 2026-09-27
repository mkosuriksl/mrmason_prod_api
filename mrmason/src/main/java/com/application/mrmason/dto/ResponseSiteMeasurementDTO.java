package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseSiteMeasurementDTO<T> {
    private String message;
    private boolean status;
    private T data;
}