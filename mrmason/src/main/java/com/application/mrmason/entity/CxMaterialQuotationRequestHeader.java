package com.application.mrmason.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="cx_material_quotation_request_header")
public class CxMaterialQuotationRequestHeader {

    @Id
    @Column(name = "material_request_id")
    private String materialRequestId;

    @Column(name = "request_date")
    private String requestDate;

    @Column(name = "request_status")
    private String requestStatus = "New";

    @Column(name = "requested_by")
    private String materialRequestRequestedBy; // customer Id

    @Column(name = "expected_delievery_date")
    private String expectedDeliveryDate;

    @Column(name = "delivery_location")
    private String deliveryLocation;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}
