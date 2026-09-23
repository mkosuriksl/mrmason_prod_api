package com.application.mrmason.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialMasterProductResponseDto {

    private String productCategory;
    private String productSubCategory;
    private String brand;
    private String sku;
    private String modelName;
}
