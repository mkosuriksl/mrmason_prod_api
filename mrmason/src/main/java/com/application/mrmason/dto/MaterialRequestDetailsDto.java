package com.application.mrmason.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialRequestDetailsDto {

    @JsonProperty("order_id")
    private String orderId;

    /*@JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_date")
    private LocalDateTime updatedDate;

    @JsonProperty("expected_delievery_date")
    private String expectedDeliveryDate;

    @JsonProperty("delivery_location")
    private String deliveryLocation;

    @JsonProperty("pincode")
    private String pincode;

    public static class Datas {

        @JsonProperty("product_category")
        private String productCategory;

        @JsonProperty("product_sub_category")
        private String productSubCategory;

        @JsonProperty("brand")
        private String brand;

        @JsonProperty("total_mrp")
        private String totalMrp;

        @JsonProperty("order_quantity")
        private int orderQty;

        @JsonProperty("mrp")
        private Double mrp;

        @JsonProperty("skuId_userId")
        private String skuIdUserId;

        @JsonProperty("line_item_id")
        private String lineItemId;
    }*/
}
