package com.application.mrmason.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
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
public class CxQuotationRequestDto {

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_date")
    private LocalDateTime updatedDate;

    @JsonProperty("expected_delievery_date")
    private String expectedDeliveryDate;

    @JsonProperty("delivery_location")
    private String deliveryLocation;

    @JsonProperty("pincode")
    private String pincode;

    @JsonProperty("header")
    private List<CxQuotationHeader> header;

    @JsonProperty("header_details")
    private List<CxQuotationHeaderDetail> headerDetail;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CxQuotationHeader {

        @JsonProperty("material_request_id")
        private String material_requestId;

        @JsonProperty("requested_date")
        private String requestDate;

        @JsonProperty("request_status")
        private String requestStatus = "New";

        @JsonProperty("requested_by")
        private String materialRequestRequestedBy;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CxQuotationHeaderDetail {

        @JsonProperty("quotation_request_line_id")
        private String quotationRequestLineId;

        @JsonProperty("quotation_id")
        private String quotationId;

        @JsonProperty("product_category")
        private String productCategory;

        @JsonProperty("product_sub_category")
        private String productSubCategory;

        @JsonProperty("brand")
        private String brand;

        @JsonProperty("sku")
        private String sku;

        @JsonProperty("product_name")
        private String productName;

        @JsonProperty("quantity")
        private String quantity;
    }
}