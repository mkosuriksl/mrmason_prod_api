package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.PlumbingMaster;

@Repository
public interface PlumbingMasterRepository
        extends JpaRepository<PlumbingMaster, String> {

    List<PlumbingMaster> findByStore_StoreId(String storeId);
}