package com.application.mrmason.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreCategoryBrandMasterRequestDto {

    @JsonProperty("storeid_mc_sub_mc_brand")
    private String storeCategorySubMaterialCategoryBrand;

    @JsonProperty("material_category")
    private String materialCategory;

    @JsonProperty("sub_material_category")
    private String subMaterialCategory;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("store_id")
    private String storeId;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_date")
    private LocalDate updatedDate;
}
