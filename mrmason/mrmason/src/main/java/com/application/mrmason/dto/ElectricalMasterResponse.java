package com.application.mrmason.dto;

import java.time.LocalDateTime;

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
public class ElectricalMasterResponse {

    // ============================================================
    // PRIMARY KEY
    // ============================================================

    private String userIdStoreIdSku;


    // ============================================================
    // STORE
    // ============================================================

    private String storeId;


    // ============================================================
    // PRODUCT CATEGORY
    // ============================================================

    private String productCategory;


    // ============================================================
    // PRODUCT SUB CATEGORY
    // ============================================================

    private ProductSubCategory productSubCategory;


    // ============================================================
    // AUDIT
    // ============================================================

    private String updatedBy;

    private LocalDateTime updatedDate;


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