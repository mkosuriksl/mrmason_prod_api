package com.application.mrmason.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialResponseDetailsDto {

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_date")
    private LocalDateTime updatedDate;

    @JsonProperty("expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    @JsonProperty("delivery_location")
    private String deliveryLocation;

    @JsonProperty("pincode")
    private String pincode;

    @JsonProperty("customer_retailer_details")
    private List<CustomerRetailerDetails> lineItems;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomerRetailerDetails {

        @JsonProperty("product_category")
        private String productCategory;

        @JsonProperty("product_sub_category")
        private String productSubCategory;

        @JsonProperty("brand")
        private String brand;

        @JsonProperty("total_mrp")
        private Double totalMrp;

        @JsonProperty("order_quantity")
        private int orderQty;

        @JsonProperty("mrp")
        private Double mrp;

        @JsonProperty("model_name")
        private String modelName;

        @JsonProperty("skuId_userId")
        private String skuIdUserId;

        @JsonProperty("line_item_id")
        private String lineItemId;
    }
}
