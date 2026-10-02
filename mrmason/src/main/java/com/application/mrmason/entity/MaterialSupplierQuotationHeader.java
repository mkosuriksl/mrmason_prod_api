package com.application.mrmason.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.application.mrmason.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "material_supplier_quotation_header")
public class MaterialSupplierQuotationHeader {

	@Id
	@Column(name = "qutotation_id")
	private String quotationId;

	@Column(name = "cmatmaterial_requestid")
	private String cmatRequestId;

	@Column(name = "quoted_amount")
	private BigDecimal quotedAmount;
	
	@Column(name = "supplier_id")
	private String supplierId; 

	@Column(name = "quoted_date")
	private LocalDate quotedDate; 

	@Column(name = "updated_date")
	private LocalDate updatedDate; 
	
	@Column(name = "invoice_number")
	private String invoiceNumber;
	
	@Column(name = "invoice_status")
	private Status invoiceStatus;
	
	@Column(name = "quotation_status")
	private Status quotationStatus;
	
	@Column(name = "invoice_date")
	private LocalDate invoiceDate;
	
	@Column(name = "updated_by")
	private String updatedBy;
	
}
