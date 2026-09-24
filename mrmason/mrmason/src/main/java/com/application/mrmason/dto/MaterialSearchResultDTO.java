package com.application.mrmason.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialSearchResultDTO {

    private String sku;
    private String brand;
    private String modelNo;
    private String modelName;
    private String materialCategory;
    private String materialSubCategory;
}