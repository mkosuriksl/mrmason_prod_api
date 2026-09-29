package com.application.mrmason.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import lombok.Data;

@Data
public class CustomerOrderDetailsDto {

	private String orderlineId;
	private String brand;
	private String skuIdUserId;
	private Double  mrp;
	private Integer discount;
	private Integer gst;
	private Double total;
	private Integer orderQty;
	private String shape;
	private BigDecimal width;
	private BigDecimal size;
	private BigDecimal thickness;
//	private String prescriptionRequired;
/*	private Date updatedDate;
	private String updatedBy;*/
	private String msUserId;

}
