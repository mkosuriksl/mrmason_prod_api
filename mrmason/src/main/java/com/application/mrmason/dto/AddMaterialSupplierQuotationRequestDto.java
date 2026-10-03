package com.application.mrmason.dto;

import com.application.mrmason.entity.CustomerOrderDetailsEntity;
import com.application.mrmason.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddMaterialSupplierQuotationRequestDto {

    private String orderId;
    private OrderStatus status;
    private String msUserId;
    private String updatedBy;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deliveryDate;

    private String deliveryLocation;
    private String pincode;

    private List<CustomerOrderDetailsEntity> quotations;

}
