package com.application.mrmason.entity;

import java.time.LocalDateTime;
import java.util.Date;

import com.application.mrmason.enums.RegSource;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quotation")
public class QuotationEntity {

	@Id
	@Column(name = "req_id", unique = true)
	private String reqId;

	@Column(name = "customer_id")
	private String customerId;

	@Column(name = "product_category")
	private String productCategory;

	@Column(name = "product_sub_category")
	private String productSubCategory;

	@Column(name = "sku")
	private String unit;

	@Column(name = "brand")
	private String brand;

	@Column(name = "product_name")
	private String productName;

	@Column(name = "quoted_amount")
	private Integer quotedAmount;

	@Column(name = "sp_id")
	private String servicePersonId;

	@Column(name = "expected_delievery_date")
	private String expectedDeliveryDate;

	@Column(name = "delivery_location")
	private String deliveryLocation;

	@Column(name = "pincode")
	private String pincode;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "status")
	private String status;

	@Column(name="regSource")
	@Enumerated(EnumType.STRING)
	private RegSource regSource;



}


