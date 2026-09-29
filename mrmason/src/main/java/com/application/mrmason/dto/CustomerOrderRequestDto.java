package com.application.mrmason.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class CustomerOrderRequestDto {
	private String cId;
	private String orderUpdatedDate;
	private String orderUpdatedBy;
	private LocalDate deliveryDate;
	private String deliveryLocation;
	private String pincode;
	private List<CustomerOrderDetailsDto> orderDetailsList;
}
