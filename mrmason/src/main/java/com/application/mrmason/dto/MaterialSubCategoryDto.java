package com.application.mrmason.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialSubCategoryDto { // sub material category dto

    private String subCategory;
    private List<BrandGroupDto> brands;
}
