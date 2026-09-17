package com.application.mrmason.repository;

import com.application.mrmason.dto.CxQuotationResponseDto;
import com.application.mrmason.entity.CxQuotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CxQuotationRepository extends JpaRepository<CxQuotation,String> {

    List<CxQuotation> findByUpdatedBy(String updatedBy);

    List<CxQuotation> findByRequestId(String requestId);


    @Query("SELECT cq FROM CxQuotation cq WHERE " +
            "(:productCategory IS NULL OR :productCategory ='' OR cq.productCategory=:productCategory) AND " +
            "(:productSubCategory IS NULL OR :productSubCategory ='' OR cq.productSubCategory=:productSubCategory) AND " +
            "(:brand IS NULL OR :brand='' OR cq.brand=:brand) AND " +
            "(:stockKeepingUnit IS NULL OR :stockKeepingUnit ='' OR cq.stockKeepingUnit=:stockKeepingUnit) AND " +
            "(:productName IS NULL OR :productName='' OR cq.productName=:productName) AND " +
            "(:quantity IS NULL OR :quantity='' OR cq.quantity=:quantity) AND " +
            "(:pincode IS NULL OR :pincode='' OR cq.pincode=:pincode) AND " +
            "(:updatedBy IS NULL OR :updatedBy='' OR cq.updatedBy=:updatedBy) AND " +
            "(:expectedDeliveryDate IS NULL OR :expectedDeliveryDate='' OR cq.expectedDeliveryDate>=:expectedDeliveryDate ) AND " +
            "(:updatedDate IS NULL OR cq.updatedDate>=:updatedDate )")
    List<CxQuotation> findByFilters (
        @Param("productCategory") String productCategory ,
        @Param("productSubCategory") String productSubCategory ,
        @Param("brand") String brand,
        @Param("stockKeepingUnit") String stockKeepingUnit,
        @Param("productName") String productName,
        @Param("quantity") String quantity,
        @Param("pincode") String pincode,
        @Param("updatedBy") String updatedBy,
        @Param("expectedDeliveryDate") String expectedDeliveryDate,
        @Param("updatedDate") LocalDateTime updatedDate
    );


}
