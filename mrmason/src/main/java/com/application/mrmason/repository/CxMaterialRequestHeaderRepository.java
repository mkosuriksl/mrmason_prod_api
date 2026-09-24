package com.application.mrmason.repository;

import com.application.mrmason.dto.CxQuotationResponseDto;
import com.application.mrmason.entity.CxMaterialQuotationRequestHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CxMaterialRequestHeaderRepository extends JpaRepository<CxMaterialQuotationRequestHeader, String> {


    @Query("SELECT h FROM CxMaterialQuotationRequestHeader h " +
            "LEFT JOIN CustomerRegistration c ON h.updatedBy = c.userid " +
            "WHERE c.userid = :customerId")
    List<CxMaterialQuotationRequestHeader> findByCustomerId(@Param("customerId") String customerId);
}