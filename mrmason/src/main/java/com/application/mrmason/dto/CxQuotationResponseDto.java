package com.application.mrmason.dto;

import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
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
public class CxQuotationResponseDto {

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "expected_delievery_date")
    private String expectedDeliveryDate;

    @Column(name = "delivery_location")
    private String deliveryLocation;

    @Column(name = "pincode")
    private String pincode;

    @JsonProperty("header_item")
    private List<CxQuotationHeader> headerlist;

    @JsonProperty("header_details")
    private List<CxQuotationHeaderDetail> headerDetail;

    @JsonProperty("customer_details")
    private CustomerDetails customerDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CxQuotationHeader {

        @Column(name = "material_request_id")
        private String materialRequestId;

        @Column(name = "requested_date")
        private String requestDate;

        @Column(name = "request_status")
        private String requestStatus = "New";

        @Column(name = "requested_by")
        private String materialRequestRequestedBy;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CxQuotationHeaderDetail {

        @Column(name="quotation_request_line_id")
        private String quotationRequestLineId;

        @Column(name = "quotation_id")
        private String quotationId;

        @Column(name = "product_category")
        private String productCategory;

        @Column(name = "product_sub_category")
        private String productSubCategory;

        @Column(name = "brand")
        private String brand;

        @Column(name = "sku")
        private String sku;

        @Column(name = "product_name")
        private String productName;

        @Column(name = "quantity")
        private String quantity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDetails {
        private String name;
        private String email;
        private String mobile;
        private String userId;
        private UserType userType;
        private RegSource regSource;
    }
}