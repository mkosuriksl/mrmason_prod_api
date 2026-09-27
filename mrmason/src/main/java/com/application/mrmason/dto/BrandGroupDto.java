package com.application.mrmason.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandGroupDto {

    private String brand;
    private List<MaterialItemDto> items;
}
