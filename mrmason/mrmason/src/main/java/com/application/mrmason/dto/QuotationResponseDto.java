package com.application.mrmason.dto;

import com.application.mrmason.enums.RegSource;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuotationResponseDto {

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("expected_delievery_date")
    private String expectedDeliveryDate;

    @JsonProperty("delivery_location")
    private String deliveryLocation;

    @JsonProperty("pincode")
    private String pincode;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_date")
    private Date updatedDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("regSource")
    @Enumerated(EnumType.STRING)
    private RegSource regSource;

    @JsonProperty("items")
    private List<QuotationItem> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuotationItem{

        @JsonProperty("req_id")
        private String reqId;


        @JsonProperty("product_category")
        private String productCategory;

        @JsonProperty("product_sub_category")
        private String productSubCategory;

        @JsonProperty("sku")
        private String unit;

        @JsonProperty("brand")
        private String brand;

        @JsonProperty("product_name")
        private String productName;

        @JsonProperty("quoted_amount")
        private Integer quotedAmount;

        @JsonProperty("sp_id")
        private String servicePersonId;

    }
}