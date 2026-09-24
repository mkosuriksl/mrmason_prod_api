package com.application.mrmason.repository;

import com.application.mrmason.entity.CxMaterialQuotationRequestHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CxQuotationRepository extends JpaRepository<CxMaterialQuotationRequestHeader, String> {

/*    List<CxMaterialQuotationRequestHeader> findByUpdatedBy(String updatedBy);*/

/*    List<CxMaterialQuotationRequestHeader> findByRequestId(String requestId);*/

    /*@Query("SELECT cq FROM CxMaterialQuotationRequestHeader cq WHERE " +
            "(:productCategory IS NULL OR :productCategory = '' OR cq.productCategory = :productCategory) AND " +
            "(:productSubCategory IS NULL OR :productSubCategory = '' OR cq.productSubCategory = :productSubCategory) AND " +
            "(:brand IS NULL OR :brand = '' OR cq.brand = :brand) AND " +
            "(:stockKeepingUnit IS NULL OR :stockKeepingUnit = '' OR cq.stockKeepingUnit = :stockKeepingUnit) AND " +
            "(:productName IS NULL OR :productName = '' OR cq.productName = :productName) AND " +
            "(:quantity IS NULL OR :quantity = '' OR cq.quantity = :quantity) AND " +
            "(:pincode IS NULL OR :pincode = '' OR cq.pincode = :pincode) AND " +
            "(:updatedBy IS NULL OR :updatedBy = '' OR cq.updatedBy = :updatedBy) AND " +
            "(:expectedDeliveryDate IS NULL OR :expectedDeliveryDate = '' OR cq.expectedDeliveryDate >= :expectedDeliveryDate) AND " +
            "(:updatedDate IS NULL OR cq.updatedDate >= :updatedDate)")
    List<CxMaterialQuotationRequestHeader> findByFilters(
            @Param("productCategory") String productCategory,
            @Param("productSubCategory") String productSubCategory,
            @Param("brand") String brand,
            @Param("stockKeepingUnit") String stockKeepingUnit,
            @Param("productName") String productName,
            @Param("quantity") String quantity,
            @Param("pincode") String pincode,
            @Param("updatedBy") String updatedBy,
            @Param("expectedDeliveryDate") String expectedDeliveryDate,
            @Param("updatedDate") LocalDateTime updatedDate
    );*/

    @Query(value = "SELECT material_request_id FROM cx_material_quotation_request_header " +
            "WHERE material_request_id LIKE CONCAT(:prefix, '%') " +
            "ORDER BY material_request_id DESC LIMIT 1",
            nativeQuery = true)
    Optional<String> findLastMaterialRequestId(@Param("prefix") String prefix);
}