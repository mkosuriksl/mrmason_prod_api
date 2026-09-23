package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import com.application.mrmason.dto.MaterialMasterProductResponseDto;
import com.application.mrmason.dto.MaterialSearchResultDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.AdminMaterialMaster;

public interface AdminMaterialMasterRepository extends JpaRepository<AdminMaterialMaster, String> {

	Optional<AdminMaterialMaster> findBySkuId(String skuId);

	List<AdminMaterialMaster> findByUpdatedBy(@Param("updatedBy") String updatedBy);

	@Query("SELECT a FROM AdminMaterialMaster a WHERE a.skuId = :skuId")
	List<AdminMaterialMaster> findBySkuIds(@Param("skuId") String skuId);



		@Query("SELECT new com.application.mrmason.dto.MaterialSearchResultDTO(m.modelName, m.sku) " +
				"FROM MaterialMaster m " +
				"WHERE LOWER(m.materialCategory) = LOWER(:category) " +
				"  AND LOWER(m.materialSubCategory) = LOWER(:subCategory) " +
				"  AND LOWER(m.brand) = LOWER(:brand) " +
				"  AND (LOWER(m.modelName) LIKE LOWER(CONCAT(:userInput, '%')) " +
				"       OR LOWER(m.sku) LIKE LOWER(CONCAT(:userInput, '%')))")
		List<MaterialSearchResultDTO> findMatchingModelNameAndSku(
				@Param("category") String category,
				@Param("subCategory") String subCategory,
				@Param("brand") String brand,
				@Param("userInput") String userInput
		);

/*		@Query("SELECT new com.application.mrmason.dto.MaterialMasterProductResponseDto (m.materialCategory, m.materialSubCategory, m.brand, m.modelName, m.sku) "+
				"FROM MaterialMaster m " +
				"WHERE LOWER(m.materialCategory) = LOWER(:materialCategory) " +
				"  AND LOWER(m.materialSubCategory) = LOWER(:materialSubCategory) " +
				"  AND LOWER(m.brand) = LOWER(:brand) " +
				" AND LOWER(m.modelName) = LOWER(:modelName)" +
				"AND LOWER(m.sku) = LOWER(:sku)")
		List<MaterialMasterProductResponseDto> findAllMaterial();*/

	@Query("SELECT new com.application.mrmason.dto.MaterialMasterProductResponseDto(" +
			"m.materialCategory, m.materialSubCategory, m.brand, m.modelName, m.sku) " +
			"FROM MaterialMaster m")
	List<MaterialMasterProductResponseDto> findAllMaterial();

}
