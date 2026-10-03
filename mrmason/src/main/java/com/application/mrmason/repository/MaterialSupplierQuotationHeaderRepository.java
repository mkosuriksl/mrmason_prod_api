package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.MaterialSupplierQuotationHeader;
import com.application.mrmason.enums.Status;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;


public interface MaterialSupplierQuotationHeaderRepository extends JpaRepository<MaterialSupplierQuotationHeader, String> {

	 @Modifying
	    @Transactional
	    @Query("UPDATE MaterialSupplierQuotationHeader h SET h.invoiceStatus = :invoiceStatus, h.invoiceNumber = :invoiceNumber WHERE h.quotationId = :quotationId")
	    int updateInvoiceStatusByQuotationId(@Param("quotationId") String quotationId,
	                                         @Param("invoiceStatus") Status invoiceStatus,
	                                         @Param("invoiceNumber") String invoiceNumber);

	@Query("SELECT h FROM MaterialSupplierQuotationHeader h WHERE h.cmatRequestId = :cmatRequestId AND h.supplierId = :supplierId")
	MaterialSupplierQuotationHeader findByCmatRequestIdAndSupplierId(@Param("cmatRequestId") String cmatRequestId, @Param("supplierId") String supplierId);

	List<MaterialSupplierQuotationHeader> findBySupplierId(String supplierId);

	MaterialSupplierQuotationHeader findByCmatRequestId(String cmatRequestId);

	@Query("SELECT q.quotationId FROM MaterialSupplierQuotationHeader q " +
			"WHERE q.quotationId LIKE CONCAT(:prefix, '%') " +
			"ORDER BY q.quotationId DESC LIMIT 1")
	Optional<String> findLastQuotationIdByPrefix(@Param("prefix") String prefix);


}
