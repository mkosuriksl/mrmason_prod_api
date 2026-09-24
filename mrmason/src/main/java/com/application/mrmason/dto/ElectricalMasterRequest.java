package com.application.mrmason.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectricalMasterRequest {

    // ============================================================
    // PRODUCT CATEGORY
    // ============================================================

    private String productCategory;


    // ============================================================
    // STORE
    // ============================================================

    private String storeId;


    // ============================================================
    // PRODUCT SUB CATEGORY
    // ============================================================

    private ProductSubCategory productSubCategory;


    // ============================================================
    // NESTED PRODUCT
    // ============================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductSubCategory {

        private String sku;

        private String productName;

        private String productDescription;

        private String dimensions;
    }
}