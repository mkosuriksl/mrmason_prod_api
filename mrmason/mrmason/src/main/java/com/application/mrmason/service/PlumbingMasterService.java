package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.PlumbingMasterRequest;
import com.application.mrmason.dto.PlumbingMasterResponse;

public interface PlumbingMasterService {

    // ============================================================
    // CREATE
    // ============================================================

<<<<<<< HEAD
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

=======
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
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528

    // ============================================================
    // UPDATE
    // ============================================================

    PlumbingMasterResponse update(
            String userIdStoreIdSku,
            PlumbingMasterRequest request);
<<<<<<< HEAD


    // ============================================================
    // GET FOR ALL USERS
    // ============================================================

    List<PlumbingMasterResponse> getForAll();
=======
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
}