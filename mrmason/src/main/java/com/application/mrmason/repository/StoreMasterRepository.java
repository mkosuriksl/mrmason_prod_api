package com.application.mrmason.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.StoreMaster;

@Repository
public interface StoreMasterRepository
        extends JpaRepository<StoreMaster, String> {

    boolean existsByStoreId(String storeId);

    boolean existsByGst(String gst);

    Optional<StoreMaster> findByStoreId(String storeId);
}