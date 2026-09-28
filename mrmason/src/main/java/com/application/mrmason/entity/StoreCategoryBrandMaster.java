package com.application.mrmason.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_category_brand_master")
public class StoreCategoryBrandMaster {

    @Id
    @Column(name = "storeid_mc_sub_mc_brand")
    private String storeCategorySubMaterialCategoryBrand;

    @Column(name = "material_category")
    private String materialCategory;

    @Column(name = "sub_material_category")
    private String subMaterialCategory;

    @Column(name = "brand")
    private String brand;

    @Column(name = "store_id")
    private String storeId;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDate updatedDate;
}
