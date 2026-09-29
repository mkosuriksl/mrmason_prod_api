package com.application.mrmason.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import lombok.Data;
import org.springframework.cglib.core.Local;

@Data
public class CustomerGetOrderResponseDTO {
	private String orderId;
	private String customerId;
	private LocalDate deliveryDate;
	private String deliveryLocation;
	private String pincode;
	private String updatedBy;
	private Date updatedDate;
	private List<CustomerOrderDetailsDto> orderDetailsList;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
