package com.application.mrmason.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CxQuotationRequestDto {

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("product_category")
    private String productCategory;

    @JsonProperty("product_sub_category")
    private String productSubCategory;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("stock_keeping_unit")
    private String stockKeepingUnit;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("quantity")
    private String quantity;

    @JsonProperty("expected_delievery_date")
    private String expectedDeliveryDate;

    @JsonProperty("delivery_location")
    private String DeliveryLocation;

    @JsonProperty("pincode")
    private String pincode;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_date")
    private LocalDateTime updatedDate;
}
