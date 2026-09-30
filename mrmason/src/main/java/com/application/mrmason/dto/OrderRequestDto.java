package com.application.mrmason.dto;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class OrderRequestDto {
    private String customerCartOrderId;
    private String customerId;
    private String location;
    private String deliveryMethod;
    private String userIdstoreId; // MS_order id
    private String expectedDeliveryDate;
    private String pincode;
    private String totalMrp;
    private List<OrderDetailsDto> orderDetailsList;

}


