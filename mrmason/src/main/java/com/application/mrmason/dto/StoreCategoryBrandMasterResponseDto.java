package com.application.mrmason.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreCategoryBrandMasterResponseDto {

    @JsonProperty("store_id")
    private String storeId;

    @JsonProperty("storeid_userid")
    private String storeIdUserId;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_date")
    private LocalDate updatedDate;

    @JsonProperty("data")
    private List<CategoryData> data;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryData {

        @JsonProperty("storeid_mc_sub_mc_brand")
        private String storeCategorySubMaterialCategoryBrand;



        @JsonProperty("material_category")
        private String materialCategory;

        @JsonProperty("sub_material_categories")
        private List<SubMaterialCategoryDto> subMaterialCategories;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubMaterialCategoryDto {

        @JsonProperty("sub_material_category")
        private String subMaterialCategory;

        @JsonProperty("brands")
        private List<BrandListDto> brands;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BrandListDto {

        @JsonProperty("brand")
        private String brand;
    }
}