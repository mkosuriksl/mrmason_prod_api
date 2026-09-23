package com.application.mrmason.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CxQuotationResponseDto {

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_date")
    private LocalDateTime updatedDate;

    @JsonProperty("expected_delivery_date")
    private String expectedDeliveryDate;

    @JsonProperty("delivery_location")
    private String deliveryLocation;

    @JsonProperty("pincode")
    private String pincode;

    @JsonProperty("item_list")
    private List<QuotationItem> itemList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class QuotationItem {

        @JsonProperty("id")
        private Long id;

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

    }
}