package com.application.mrmason.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "store_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreMaster {

    // =====================================================
    // STORE ID
    // Input from UI
    // =====================================================

    @Id
    @Column(name = "storeid", nullable = false, unique = true)
    private String storeId;


    // =====================================================
    // STORE INFORMATION
    // Input from UI
    // =====================================================

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "gst")
    private String gst;

    @Column(name = "address")
    private String address;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "state")
    private String state;

    @Column(name = "district")
    private String district;

    @Column(name = "town")
    private String town;

    @Column(name = "land_mark")
    private String landMark;


    // =====================================================
    // VERIFY STATUS
    // Default value = INACTIVE
    // =====================================================

    @Column(name = "verify_status")
    private String verifyStatus;


    // =====================================================
    // CODE DERIVED FIELDS
    // These are NOT entered from UI
    // =====================================================

    @Column(name = "storeid_userid")
    private String storeIdUserId;

    @Column(name = "updatedby")
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;


    // =====================================================
    // PRE PERSIST
    // Runs before inserting into database
    // =====================================================

    @PrePersist
    protected void onCreate() {

        // Default verify status
        if (verifyStatus == null || verifyStatus.isBlank()) {
            verifyStatus = "INACTIVE";
        }

        // Current date and time
        if (updatedDate == null) {
            updatedDate = LocalDateTime.now();
        }
    }
}