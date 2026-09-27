package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.PlumbingMasterRequest;
import com.application.mrmason.dto.PlumbingMasterResponse;

public interface PlumbingMasterService {

    // ============================================================
    // CREATE
    // ============================================================

    PlumbingMasterResponse create(
            PlumbingMasterRequest request);


    // ============================================================
    // GET FOR MATERIAL SUPPLIER
    // ============================================================

    PlumbingMasterResponse getForMs(
            String storeId,
            String updatedBy,
            String productCategory,
            String productSubCategory);


    // ============================================================
    // UPDATE
    // ============================================================

    PlumbingMasterResponse update(
            String userIdStoreIdSku,
            PlumbingMasterRequest request);


    // ============================================================
    // GET FOR ALL USERS
    // ============================================================

    List<PlumbingMasterResponse> getForAll();
}