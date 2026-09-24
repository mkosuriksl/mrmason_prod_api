package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.PlumbingMaster;

@Repository
public interface PlumbingMasterRepository
        extends JpaRepository<PlumbingMaster, String> {

    // ============================================================
    // FIND BY PRIMARY KEY
    // ============================================================

    Optional<PlumbingMaster> findByUserIdStoreIdSku(
            String userIdStoreIdSku);


    // ============================================================
    // FIND ALL RECORDS BELONGING TO MATERIAL SUPPLIER
    // ============================================================

    List<PlumbingMaster> findByUpdatedBy(
            String updatedBy);


    // ============================================================
    // FIND RECORDS BY STORE
    // ============================================================

    List<PlumbingMaster> findByStore_StoreId(
            String storeId);


    // ============================================================
    // FIND STORE RECORDS BELONGING TO MATERIAL SUPPLIER
    // ============================================================

    List<PlumbingMaster> findByStore_StoreIdAndUpdatedBy(
            String storeId,
            String updatedBy);


    // ============================================================
    // DUPLICATE CHECK
    // ============================================================

    boolean existsByUserIdStoreIdSku(
            String userIdStoreIdSku);
}