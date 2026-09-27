package com.application.mrmason.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "electrical_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectricalMaster {

    // ============================================================
    // PRIMARY KEY
    // ============================================================

    @Id
    @Column(
        name = "user_id_store_id_sku",
        nullable = false,
        unique = true
    )
    private String userIdStoreIdSku;


    // ============================================================
    // PRODUCT CATEGORY
    // ============================================================

    @Column(
        name = "product_category",
        nullable = false
    )
    private String productCategory;


    // ============================================================
    // PRODUCT DETAILS
    // ============================================================

    @Column(
        name = "sku",
        nullable = false
    )
    private String sku;

    @Column(
        name = "product_name",
        nullable = false
    )
    private String productName;

    @Column(
        name = "product_description"
    )
    private String productDescription;

    @Column(
        name = "dimensions"
    )
    private String dimensions;


    // ============================================================
    // STORE
    // ============================================================

    /*
     * electrical_master.store_id
     *          |
     *          v
     * store_master.storeid
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "store_id",
        referencedColumnName = "storeid",
        nullable = false
    )
    private StoreMaster store;


    // ============================================================
    // AUDIT
    // ============================================================

    @Column(
        name = "updated_by",
        nullable = false
    )
    private String updatedBy;

    @Column(
        name = "updated_date"
    )
    private LocalDateTime updatedDate;
}