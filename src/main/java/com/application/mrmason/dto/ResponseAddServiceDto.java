package com.application.mrmason.dto;

import lombok.Data;

@Data
public class ResponseAddServiceDto {
	private String message;
	private boolean status;
	private AddServicesDto1 AddServicesData;
}
