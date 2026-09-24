package com.application.mrmason.repository;

import java.util.List;
<<<<<<< HEAD
import java.util.Optional;
=======
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.PlumbingMaster;

@Repository
public interface PlumbingMasterRepository
        extends JpaRepository<PlumbingMaster, String> {

<<<<<<< HEAD
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
=======
    List<PlumbingMaster> findByStore_StoreId(String storeId);
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
}