
package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.mrmason.dto.PlumbingMasterRequest;
import com.application.mrmason.dto.PlumbingMasterResponse;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.PlumbingMaster;
import com.application.mrmason.entity.StoreMaster;
import com.application.mrmason.repository.PlumbingMasterRepository;
import com.application.mrmason.repository.StoreMasterRepository;
import com.application.mrmason.service.PlumbingMasterService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PlumbingMasterServiceImpl
        implements PlumbingMasterService {

    private final PlumbingMasterRepository plumbingMasterRepository;

    private final StoreMasterRepository storeMasterRepository;

    // ============================================================
    // CREATE
    // ============================================================

    @Override
    public PlumbingMasterResponse createPlumbingMaster(
            PlumbingMasterRequest request) {

        // --------------------------------------------------------
        // CREATE REQUIRES ALL REQUIRED FIELDS
        // --------------------------------------------------------

        validateCreateRequest(request);

        // --------------------------------------------------------
        // GET LOGGED-IN USER
        // --------------------------------------------------------

        MaterialSupplierQuotationUser loggedInUser =
                getLoggedInMSUser();

        String userId =
                loggedInUser.getBodSeqNo();

        String updatedBy =
                loggedInUser.getBodSeqNo();

        // --------------------------------------------------------
        // FIND STORE
        // --------------------------------------------------------

        StoreMaster store =
                storeMasterRepository
                        .findById(request.getStoreId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Store not found with storeId: "
                                                + request.getStoreId()));

        // --------------------------------------------------------
        // PRODUCT DETAILS
        // --------------------------------------------------------

        PlumbingMasterRequest.ProductSubCategory product =
                request.getProductSubCategory();

        // --------------------------------------------------------
        // CREATE UNIQUE ID
        //
        // Example:
        //
        // MS2026091611171674_1_70011505
        // --------------------------------------------------------

        String userIdStoreIdSku =
                userId
                        + "_"
                        + request.getStoreId()
                        + "_"
                        + product.getSku();

        // --------------------------------------------------------
        // CHECK DUPLICATE
        // --------------------------------------------------------

        if (plumbingMasterRepository
                .existsById(userIdStoreIdSku)) {

            throw new RuntimeException(
                    "Plumbing master already exists with ID: "
                            + userIdStoreIdSku);
        }

        // --------------------------------------------------------
        // CURRENT DATE TIME
        // --------------------------------------------------------

        LocalDateTime now =
                LocalDateTime.now();

        // --------------------------------------------------------
        // BUILD ENTITY
        // --------------------------------------------------------

        PlumbingMaster plumbingMaster =
                PlumbingMaster.builder()
                        .userIdStoreIdSku(
                                userIdStoreIdSku)

                        .productCategory(
                                request.getProductCategory())

                        .sku(
                                product.getSku())

                        .productName(
                                product.getProductName())

                        .productDescription(
                                product.getProductDescription())

                        .dimensions(
                                product.getDimensions())

                        .store(store)

                        .updatedBy(
                                updatedBy)

                        .updatedDate(
                                now)

                        .build();

        // --------------------------------------------------------
        // SAVE
        // --------------------------------------------------------

        PlumbingMaster saved =
                plumbingMasterRepository.save(
                        plumbingMaster);

        // --------------------------------------------------------
        // RESPONSE
        // --------------------------------------------------------

        return convertToResponse(saved);
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public PlumbingMasterResponse getById(
            String userIdStoreIdSku) {

        PlumbingMaster plumbingMaster =
                plumbingMasterRepository
                        .findById(userIdStoreIdSku)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Plumbing master not found with ID: "
                                                + userIdStoreIdSku));

        return convertToResponse(plumbingMaster);
    }

    // ============================================================
    // GET BY STORE ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<PlumbingMasterResponse> getByStoreId(
            String storeId) {

        List<PlumbingMaster> list =
                plumbingMasterRepository
                        .findByStore_StoreId(storeId);

        return list.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ============================================================
    // GET ALL
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<PlumbingMasterResponse> getAll() {

        List<PlumbingMaster> list =
                plumbingMasterRepository.findAll();

        return list.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ============================================================
    // UPDATE
    //
    // PARTIAL UPDATE
    //
    // Only fields present in request will be updated.
    //
    // Example:
    //
    // {
    //     "storeId": "1"
    // }
    //
    // Only store will be updated.
    // ============================================================

    @Override
    public PlumbingMasterResponse update(
            String userIdStoreIdSku,
            PlumbingMasterRequest request) {

        // --------------------------------------------------------
        // REQUEST CANNOT BE NULL
        // --------------------------------------------------------

        if (request == null) {

            throw new RuntimeException(
                    "Request cannot be null");
        }

        // --------------------------------------------------------
        // FIND EXISTING RECORD
        // --------------------------------------------------------

        PlumbingMaster plumbingMaster =
                plumbingMasterRepository
                        .findById(userIdStoreIdSku)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Plumbing master not found with ID: "
                                                + userIdStoreIdSku));

        // --------------------------------------------------------
        // GET LOGGED-IN USER
        // --------------------------------------------------------

        MaterialSupplierQuotationUser loggedInUser =
                getLoggedInMSUser();

        // ========================================================
        // UPDATE STORE
        // ========================================================

        if (request.getStoreId() != null
                && !request.getStoreId()
                        .trim()
                        .isEmpty()) {

            StoreMaster store =
                    storeMasterRepository
                            .findById(request.getStoreId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Store not found with storeId: "
                                                    + request.getStoreId()));

            plumbingMaster.setStore(store);
        }

        // ========================================================
        // UPDATE PRODUCT CATEGORY
        // ========================================================

        if (request.getProductCategory() != null
                && !request.getProductCategory()
                        .trim()
                        .isEmpty()) {

            plumbingMaster.setProductCategory(
                    request.getProductCategory());
        }

        // ========================================================
        // UPDATE PRODUCT SUB CATEGORY
        // ========================================================

        if (request.getProductSubCategory() != null) {

            PlumbingMasterRequest.ProductSubCategory product =
                    request.getProductSubCategory();

            // ----------------------------------------------------
            // SKU
            // ----------------------------------------------------

            if (product.getSku() != null
                    && !product.getSku()
                            .trim()
                            .isEmpty()) {

                plumbingMaster.setSku(
                        product.getSku());
            }

            // ----------------------------------------------------
            // PRODUCT NAME
            // ----------------------------------------------------

            if (product.getProductName() != null
                    && !product.getProductName()
                            .trim()
                            .isEmpty()) {

                plumbingMaster.setProductName(
                        product.getProductName());
            }

            // ----------------------------------------------------
            // PRODUCT DESCRIPTION
            // ----------------------------------------------------

            if (product.getProductDescription() != null) {

                plumbingMaster.setProductDescription(
                        product.getProductDescription());
            }

            // ----------------------------------------------------
            // DIMENSIONS
            // ----------------------------------------------------

            if (product.getDimensions() != null) {

                plumbingMaster.setDimensions(
                        product.getDimensions());
            }
        }

        // ========================================================
        // UPDATED BY
        // ========================================================

        plumbingMaster.setUpdatedBy(
                loggedInUser.getBodSeqNo());

        // ========================================================
        // UPDATED DATE
        // ========================================================

        plumbingMaster.setUpdatedDate(
                LocalDateTime.now());

        // ========================================================
        // SAVE
        // ========================================================

        PlumbingMaster updated =
                plumbingMasterRepository.save(
                        plumbingMaster);

        // ========================================================
        // RESPONSE
        // ========================================================

        return convertToResponse(updated);
    }

    // ============================================================
    // CREATE VALIDATION
    //
    // IMPORTANT:
    // This validation is ONLY for CREATE.
    //
    // UPDATE DOES NOT USE THIS VALIDATION.
    // ============================================================

    private void validateCreateRequest(
            PlumbingMasterRequest request) {

        if (request == null) {

            throw new RuntimeException(
                    "Request cannot be null");
        }

        // --------------------------------------------------------
        // STORE ID
        // --------------------------------------------------------

        if (request.getStoreId() == null
                || request.getStoreId()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "storeId is required");
        }

        // --------------------------------------------------------
        // PRODUCT CATEGORY
        // --------------------------------------------------------

        if (request.getProductCategory() == null
                || request.getProductCategory()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "productCategory is required");
        }

        // --------------------------------------------------------
        // PRODUCT SUB CATEGORY
        // --------------------------------------------------------

        if (request.getProductSubCategory() == null) {

            throw new RuntimeException(
                    "productSubCategory is required");
        }

        // --------------------------------------------------------
        // SKU
        // --------------------------------------------------------

        if (request.getProductSubCategory()
                        .getSku() == null
                || request.getProductSubCategory()
                        .getSku()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "SKU is required");
        }

        // --------------------------------------------------------
        // PRODUCT NAME
        // --------------------------------------------------------

        if (request.getProductSubCategory()
                        .getProductName() == null
                || request.getProductSubCategory()
                        .getProductName()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "productName is required");
        }
    }

    // ============================================================
    // GET LOGGED-IN MS USER
    // ============================================================

    private MaterialSupplierQuotationUser getLoggedInMSUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated");
        }

        Object principal =
                authentication.getPrincipal();

        // --------------------------------------------------------
        // CHECK PRINCIPAL TYPE
        // --------------------------------------------------------

        if (!(principal
                instanceof MaterialSupplierQuotationUser)) {

            throw new RuntimeException(
                    "Logged-in user is not a Material Supplier user");
        }

        return (MaterialSupplierQuotationUser) principal;
    }

    // ============================================================
    // ENTITY -> RESPONSE
    // ============================================================

    private PlumbingMasterResponse convertToResponse(
            PlumbingMaster entity) {

        PlumbingMasterResponse.ProductSubCategory product =
                PlumbingMasterResponse.ProductSubCategory
                        .builder()

                        .sku(
                                entity.getSku())

                        .productName(
                                entity.getProductName())

                        .productDescription(
                                entity.getProductDescription())

                        .dimensions(
                                entity.getDimensions())

                        .build();

        return PlumbingMasterResponse.builder()

                // ------------------------------------------------
                // PRIMARY KEY
                // ------------------------------------------------

                .userIdStoreIdSku(
                        entity.getUserIdStoreIdSku())

                // ------------------------------------------------
                // STORE
                // ------------------------------------------------

                .storeId(
                        entity.getStore().getStoreId())

                // ------------------------------------------------
                // PRODUCT CATEGORY
                // ------------------------------------------------

                .productCategory(
                        entity.getProductCategory())

                // ------------------------------------------------
                // PRODUCT SUB CATEGORY
                // ------------------------------------------------

                .productSubCategory(
                        product)

                // ------------------------------------------------
                // AUDIT
                // ------------------------------------------------

                .updatedBy(
                        entity.getUpdatedBy())

                .updatedDate(
                        entity.getUpdatedDate())

                .build();
    }
}
