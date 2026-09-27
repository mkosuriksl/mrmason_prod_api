package com.application.mrmason.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MaterialItemDto { // Material category dto

    private String skuId;
    private String modelNo;
    private String modelName;

}
