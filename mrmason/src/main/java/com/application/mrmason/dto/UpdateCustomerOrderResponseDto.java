package com.application.mrmason.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerOrderResponseDto {

    private String orderId;
    private String updatedBy;
    private Date updatedDate;
    private List<CustomerOrderDetailsDto> orderDetailsList;
}
