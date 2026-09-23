package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.ElectricalMaster;

@Repository
public interface ElectricalMasterRepository
        extends JpaRepository<ElectricalMaster, String> {

    // ============================================================
    // FIND BY PRIMARY KEY
    // ============================================================

    Optional<ElectricalMaster> findByUserIdStoreIdSku(
            String userIdStoreIdSku);


    // ============================================================
    // DUPLICATE CHECK
    // ============================================================

    boolean existsByUserIdStoreIdSku(
            String userIdStoreIdSku);


    // ============================================================
    // FIND BY MS USER
    // ============================================================

    List<ElectricalMaster> findByUpdatedBy(
            String updatedBy);


    // ============================================================
    // FIND BY STORE
    // ============================================================

    List<ElectricalMaster> findByStore_StoreId(
            String storeId);


	List<ElectricalMaster> findByStore_StoreIdAndUpdatedBy(String trim, String userId);
}