package com.application.mrmason.dto;
import java.time.LocalDate;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import com.application.mrmason.enums.OrderStatus;

@Getter 
@Setter 

public class MaterialRequestsFilterDto {

private String materialCategory;

    private String materialSubCategory;

    private String brand;

    private Date postedDateFrom;

    private Date postedDateTo;

    private LocalDate deliveryDateFrom;

    private LocalDate deliveryDateTo;

    private String deliveryLocation;

    private OrderStatus orderStatus;



}
