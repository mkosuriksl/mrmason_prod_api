package com.application.mrmason.dto;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialHeaderAndDetailedRequestDto {

    private String materialRequestId;

    @Column(name = "total_qty")
    private int totalQty;

    @Column(name = "c_email", length = 45)
    private String customerEmail;

    @Column(name = "c_name", length = 45)
    private String customerName;

    @Column(name = "date")
    private LocalDate createdDate;

    @Column(name = "update_by", length = 45)
    private String updatedBy;

    @Column(name = "updated_date", length = 45)
    private LocalDate updatedDate;

    @Column(name = "quote_id", length = 45)
    private String quoteId;

    @Column(name = "requested_by", length = 45)
    private String requestedBy;

    @Column(name = "c_moble", length = 45)
    private String customerMobile;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "delvery_location", length = 45)
    private String deliveryLocation;

    private String cMatRequestIdLineid;

    @Column(name = "c_mat_request_id")
    private String cMatRequestId;

    @Column(name = "material_category")
    private String materialCategory;

    @Column(name = "brand")
    private String brand;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_size")
    private String itemSize;

    @Column(name = "qty")
    private int qty;

    @Column(name = "order_date")
    private LocalDate orderDate;

}
