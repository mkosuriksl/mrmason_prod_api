package com.application.mrmason.repository;

import com.application.mrmason.entity.StoreCategoryBrandMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreCategoryBrandMasterRepository extends JpaRepository<StoreCategoryBrandMaster, String> {

    @Query("SELECT s.storeId FROM StoreCategoryBrandMaster s WHERE s.storeId LIKE 'STR%' ORDER BY s.storeId DESC LIMIT 1")
    Optional<String> findByStoreId();


    @Query("SELECT m FROM StoreCategoryBrandMaster m WHERE " +
            "(:materialCategory IS NULL OR :materialCategory = '' OR m.materialCategory = :materialCategory) AND " +
            "(:subMaterialCategory IS NULL OR :subMaterialCategory = '' OR m.subMaterialCategory = :subMaterialCategory) AND " +
            "(:brand IS NULL OR :brand = '' OR m.brand = :brand) AND " +
            "(:updatedBy IS NULL OR :updatedBy = '' OR m.updatedBy = :updatedBy)")
    List<StoreCategoryBrandMaster> findByFilters(
            @Param("materialCategory") String materialCategory,
            @Param("subMaterialCategory") String subMaterialCategory,
            @Param("brand") String brand,
            @Param("updatedBy") String updatedBy);


    @Query("SELECT m FROM StoreCategoryBrandMaster m WHERE m.storeId = :storeId")
    Optional<StoreCategoryBrandMaster> findByStoreId(@Param("storeId") String storeId);
}
