package com.application.mrmason.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class CustomerOrderRequestDto {
	private String cId;
	private LocalDate deliveryDate;
	private String deliveryLocation;
	private String pincode;
	private List<CustomerOrderDetailsDto> orderDetailsList;
}
