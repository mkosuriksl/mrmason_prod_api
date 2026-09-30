
package com.application.mrmason.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialGetDTO {

    private String skuId;

    private String modelNo;

    private String modelName;

    private String shape;

    private BigDecimal width;

    private BigDecimal length;

    private BigDecimal size;

    private BigDecimal thickness;

    private String status;

    private String updatedBy;

    private String materialMasterImage1;

    private String materialMasterImage2;

    private String materialMasterImage3;

    private String materialMasterImage4;

    private String materialMasterImage5;
}

