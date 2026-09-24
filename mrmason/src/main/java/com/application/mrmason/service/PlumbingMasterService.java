package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.PlumbingMasterRequest;
import com.application.mrmason.dto.PlumbingMasterResponse;

public interface PlumbingMasterService {

    // ============================================================
    // CREATE
    // ============================================================

    PlumbingMasterResponse createPlumbingMaster(
            PlumbingMasterRequest request);

    // ============================================================
    // GET BY ID
    // ============================================================

    PlumbingMasterResponse getById(
            String userIdStoreIdSku);

    // ============================================================
    // GET BY STORE
    // ============================================================

    List<PlumbingMasterResponse> getByStoreId(
            String storeId);

    // ============================================================
    // GET ALL
    // ============================================================

    List<PlumbingMasterResponse> getAll();

    // ============================================================
    // UPDATE
    // ============================================================

    PlumbingMasterResponse update(
            String userIdStoreIdSku,
            PlumbingMasterRequest request);
}