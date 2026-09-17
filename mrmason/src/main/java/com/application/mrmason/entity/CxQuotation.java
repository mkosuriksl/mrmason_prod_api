package com.application.mrmason.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="cx_quotation")
public class CxQuotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private String deliveryLocation;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}
