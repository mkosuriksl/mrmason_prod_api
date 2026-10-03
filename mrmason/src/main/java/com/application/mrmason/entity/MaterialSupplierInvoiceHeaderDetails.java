package com.application.mrmason.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.application.mrmason.enums.Status; // Ensure Status enum is imported

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "material_supplier_invoice_details")
public class MaterialSupplierInvoiceHeaderDetails {

    @Id
    @Column(name = "qutotation_id_lineid")
    private String quotationIdLineId;

    @Column(name = "cmatmaterial_requestid")
    private String cMaterialRequestId;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "gst")
    private BigDecimal gst;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "invoice_number_lineid")
    private String invoiceNumberLineId;

    // Use ORDINAL so Hibernate inserts integers instead of text strings
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "invoice_status")
    private Status invoiceStatus;

    @Column(name = "material_line_item")
    private String materialLineId;

    @Column(name = "mrp")
    private String mrp;

    @Column(name = "qutotation_id")
    private String quotationId;

    // Use ORDINAL
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "quotation_status")
    private Status quotationStatus;

    @Column(name = "quoted_amount")
    private BigDecimal quotedAmount;

    @Column(name = "quoted_date")
    private LocalDate quotedDate;

    // Use ORDINAL
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private Status status;

    @Column(name = "supplier_id")
    private String supplierId;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDate updatedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType;

}