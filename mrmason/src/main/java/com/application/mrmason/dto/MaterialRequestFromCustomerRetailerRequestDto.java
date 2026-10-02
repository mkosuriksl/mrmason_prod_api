package com.application.mrmason.dto;


import com.application.mrmason.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialRequestFromCustomerRetailerRequestDto {




    private String orderId;

    private String customerCartOrderId;
    private String customerId;
    private String retailerId;
    private String orderUpdatedBy;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDate orderDate;

    private Date orderUpdatedDate;

    @Column(name = "delivery_method")
    private String deliveryMethod;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus paymentStatus;

    @Column(name="total_mrp")
    private Double totalMrp;

    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    @Column(name = "delivery_location")
    private String deliveryLocation;

    //Detail Entity

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RetailerDetailsDto{
        private String lineItemId;

        private String customerCartOrderLineId;
        private String brand;
        private String modelName;
        private String materialCategory;
        private String materialSubCategory;
        private int orderQty;
        private Double mrp;
        private float discount;
        private float gst;
        private Double totalAmount;
        private String manufactureName;
        private String skuIdUserId;
        private Date updatedDate;
        private String updatedBy;
        private String userId;

    }


}
