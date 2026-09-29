package com.application.mrmason.dto;

import java.time.LocalDate;

import com.application.mrmason.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MaterialRequestbyCustomerResponseHomePageDto { 

    private String orderId;
    private String customerId;
    private LocalDate expectedDeliveryDate;
    private String deliveryLocation;
    private OrderStatus orderStatus;
}

