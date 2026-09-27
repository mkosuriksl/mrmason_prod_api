package com.application.mrmason.repository;

import com.application.mrmason.entity.CxMaterialQuotationRequestHeaderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CxQuotationHeaderDetailRepository extends JpaRepository<CxMaterialQuotationRequestHeaderDetails, String> {

    List<CxMaterialQuotationRequestHeaderDetails> findByQuotationRequestLineIdStartingWith(String prefix);

    @Query("SELECT d FROM CxMaterialQuotationRequestHeaderDetails d " +
            "JOIN CxMaterialQuotationRequestHeader h ON d.quotationRequestLineId LIKE CONCAT(h.materialRequestId, '%') " +
            "WHERE (:productCategory IS NULL OR d.productCategory = :productCategory) " +
            "AND (:productSubCategory IS NULL OR d.productSubCategory = :productSubCategory) " +
            "AND (:brand IS NULL OR d.brand = :brand) " +
            "AND (:stockKeepingUnit IS NULL OR d.sku = :stockKeepingUnit) " +
            "AND (:productName IS NULL OR d.productName = :productName) " +
            "AND (:quantity IS NULL OR d.quantity = :quantity) " +
            "AND (:pincode IS NULL OR h.pincode = :pincode) " +
            "AND (:updatedBy IS NULL OR h.updatedBy = :updatedBy) " +
            "AND (:expectedDeliveryDate IS NULL OR h.expectedDeliveryDate = :expectedDeliveryDate) " +
            "AND (:updatedDate IS NULL OR CAST(h.updatedDate AS string) LIKE CONCAT(:updatedDate, '%'))")
    List<CxMaterialQuotationRequestHeaderDetails> searchAllQuotationDetails(
            @Param("productCategory") String productCategory,
            @Param("productSubCategory") String productSubCategory,
            @Param("brand") String brand,
            @Param("stockKeepingUnit") String stockKeepingUnit,
            @Param("productName") String productName,
            @Param("quantity") String quantity,
            @Param("pincode") String pincode,
            @Param("updatedBy") String updatedBy,
            @Param("expectedDeliveryDate") String expectedDeliveryDate,
            @Param("updatedDate") String updatedDate
    );
}