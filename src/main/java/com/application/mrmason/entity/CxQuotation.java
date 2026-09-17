package com.application.mrmason.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="cx_quotation")
public class CxQuotation {

    @Id
    @Column(name = "request_id")
    private String requestId;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "product_sub_category")
    private String productSubCategory;

    @Column(name = "brand")
    private String brand;

    @Column(name = "stock_keeping_out")
    private String stockKeepingUnit;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "expected_delievery_date")
    private String expectedDeliveryDate;

    @Column(name = "delivery_location")
    private String DeliveryLocation;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}
