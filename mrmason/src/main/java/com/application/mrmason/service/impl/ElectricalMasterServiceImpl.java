
package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.mrmason.dto.ElectricalMasterRequest;
import com.application.mrmason.dto.ElectricalMasterResponse;
import com.application.mrmason.entity.ElectricalMaster;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.StoreMaster;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.ElectricalMasterRepository;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.repository.StoreMasterRepository;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.ElectricalMasterService;

@Service
public class ElectricalMasterServiceImpl
        implements ElectricalMasterService {

    // ============================================================
    // REPOSITORIES
    // ============================================================

    @Autowired
    private ElectricalMasterRepository electricalMasterRepository;

    @Autowired
    private MaterialSupplierQuotationUserDAO materialSupplierUserDAO;

    @Autowired
    private StoreMasterRepository storeMasterRepository;


    // ============================================================
    // GET LOGGED-IN MATERIAL SUPPLIER USER ID
    // ============================================================

    private String getLoggedInMaterialSupplierUserId() {

        // --------------------------------------------------------
        // GET EMAIL FROM JWT
        // --------------------------------------------------------

        String email =
                AuthDetailsProvider.getLoggedEmail();

        if (email == null
                || email.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Logged-in user not found");
        }

        String loggedInEmail =
                email.trim();


        // --------------------------------------------------------
        // FIND MATERIAL SUPPLIER
        // --------------------------------------------------------

        MaterialSupplierQuotationUser supplier =
                materialSupplierUserDAO
                        .findByEmailAndRegSource(
                                loggedInEmail,
                                RegSource.MRMASON)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Material Supplier registration "
                                                + "not found for email: "
                                                + loggedInEmail
                                                + ", regSource: MRMASON"));


        // --------------------------------------------------------
        // CHECK SUPPLIER STATUS
        // --------------------------------------------------------

        if (supplier.getStatus() == null
                || !"active".equalsIgnoreCase(
                        supplier.getStatus())) {

            throw new IllegalArgumentException(
                    "Material Supplier is not active for email: "
                            + loggedInEmail);
        }


        // --------------------------------------------------------
        // GET BOD SEQ NO
        // --------------------------------------------------------

        String userId =
                supplier.getBodSeqNo();

        if (userId == null
                || userId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "bodSeqNo not found for Material Supplier: "
                            + loggedInEmail);
        }

        return userId.trim();
    }


    // ============================================================
    // FIND STORE BELONGING TO LOGGED-IN MATERIAL SUPPLIER
    // ============================================================

    private StoreMaster getSupplierStore(
            String storeId,
            String userId) {

        if (storeId == null
                || storeId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Store ID is required");
        }

        String trimmedStoreId =
                storeId.trim();


        /*
         * We check:
         *
         * storeId
         * +
         * updatedBy = logged-in MS userId
         *
         * Therefore one Material Supplier cannot use
         * another Material Supplier's store.
         */

        return storeMasterRepository
                .findByStoreIdAndUpdatedBy(
                        trimmedStoreId,
                        userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Store not found or does not belong "
                                        + "to the logged-in Material "
                                        + "Supplier: "
                                        + trimmedStoreId)).getStore();
    }


    // ============================================================
    // CREATE ELECTRICAL MASTER
    //
    // POST
    // ONLY MS
    // ============================================================

    @Override
    @Transactional
    public ElectricalMasterResponse create(
            ElectricalMasterRequest request) {

        // --------------------------------------------------------
        // REQUEST VALIDATION
        // --------------------------------------------------------

        if (request == null) {

            throw new IllegalArgumentException(
                    "Request cannot be null");
        }


        // --------------------------------------------------------
        // STORE ID VALIDATION
        // --------------------------------------------------------

        if (request.getStoreId() == null
                || request.getStoreId()
                        .trim()
                        .isEmpty()) {

            throw new IllegalArgumentException(
                    "Store ID is required");
        }


        // --------------------------------------------------------
        // PRODUCT SUB CATEGORY VALIDATION
        // --------------------------------------------------------

        if (request.getProductSubCategory() == null) {

            throw new IllegalArgumentException(
                    "Product sub category is required");
        }


        // --------------------------------------------------------
        // PRODUCT CATEGORY VALIDATION
        // --------------------------------------------------------

        if (request.getProductCategory() != null
                && !request.getProductCategory()
                        .trim()
                        .isEmpty()) {

            if (!"Electrical".equalsIgnoreCase(
                    request.getProductCategory().trim())) {

                throw new IllegalArgumentException(
                        "Invalid product category. "
                                + "Expected Electrical");
            }
        }


        // --------------------------------------------------------
        // SKU VALIDATION
        // --------------------------------------------------------

        String sku =
                request.getProductSubCategory()
                        .getSku();

        if (sku == null
                || sku.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "SKU is required");
        }


        // --------------------------------------------------------
        // PRODUCT NAME VALIDATION
        // --------------------------------------------------------

        String productName =
                request.getProductSubCategory()
                        .getProductName();

        if (productName == null
                || productName.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Product name is required");
        }


        // --------------------------------------------------------
        // CLEAN VALUES
        // --------------------------------------------------------

        String storeId =
                request.getStoreId().trim();

        sku = sku.trim();

        productName =
                productName.trim();


        // --------------------------------------------------------
        // GET AUTHENTICATED MATERIAL SUPPLIER
        // --------------------------------------------------------

        String userId =
                getLoggedInMaterialSupplierUserId();


        // --------------------------------------------------------
        // GET SUPPLIER'S STORE
        // --------------------------------------------------------

        StoreMaster store =
                getSupplierStore(
                        storeId,
                        userId);


        // --------------------------------------------------------
        // CREATE PRIMARY KEY
        //
        // userId_storeId_sku
        //
        // Example:
        //
        // MS123_1_70011505
        // --------------------------------------------------------

        String userIdStoreIdSku =
                userId
                        + "_"
                        + storeId
                        + "_"
                        + sku;


        // --------------------------------------------------------
        // DUPLICATE CHECK
        // --------------------------------------------------------

        if (electricalMasterRepository
                .existsByUserIdStoreIdSku(
                        userIdStoreIdSku)) {

            throw new IllegalArgumentException(
                    "Electrical product already exists: "
                            + userIdStoreIdSku);
        }


        // --------------------------------------------------------
        // CREATE ENTITY
        // --------------------------------------------------------

        ElectricalMaster electricalMaster =
                new ElectricalMaster();


        // --------------------------------------------------------
        // PRIMARY KEY
        // --------------------------------------------------------

        electricalMaster.setUserIdStoreIdSku(
                userIdStoreIdSku);


        // --------------------------------------------------------
        // PRODUCT CATEGORY
        // --------------------------------------------------------

        electricalMaster.setProductCategory(
                "Electrical");


        // --------------------------------------------------------
        // SKU
        // --------------------------------------------------------

        electricalMaster.setSku(
                sku);


        // --------------------------------------------------------
        // PRODUCT NAME
        // --------------------------------------------------------

        electricalMaster.setProductName(
                productName);


        // --------------------------------------------------------
        // PRODUCT DESCRIPTION
        // --------------------------------------------------------

        String productDescription =
                request.getProductSubCategory()
                        .getProductDescription();

        if (productDescription != null) {

            electricalMaster.setProductDescription(
                    productDescription.trim());
        }


        // --------------------------------------------------------
        // DIMENSIONS
        // --------------------------------------------------------

        String dimensions =
                request.getProductSubCategory()
                        .getDimensions();

        if (dimensions != null) {

            electricalMaster.setDimensions(
                    dimensions.trim());
        }


        // --------------------------------------------------------
        // STORE RELATIONSHIP
        //
        // Entity has:
        //
        // StoreMaster store
        //
        // NOT:
        //
        // String storeId
        // --------------------------------------------------------

        electricalMaster.setStore(store);


        // --------------------------------------------------------
        // AUDIT
        // --------------------------------------------------------

        electricalMaster.setUpdatedBy(
                userId);

        electricalMaster.setUpdatedDate(
                LocalDateTime.now());


        // --------------------------------------------------------
        // SAVE
        // --------------------------------------------------------

        ElectricalMaster saved =
                electricalMasterRepository.save(
                        electricalMaster);


        return convertToResponse(saved);
    }


    // ============================================================
    // GET FOR LOGGED-IN MATERIAL SUPPLIER
    //
    // GET /api/electrical-master
    // ============================================================

   @Override
@Transactional(readOnly = true)
public List<ElectricalMasterResponse> getForMs() {

    // ============================================================
    // GET AUTHENTICATED SUPPLIER FROM JWT
    // ============================================================

    String userId =
            getLoggedInMaterialSupplierUserId();


    // ============================================================
    // GET ELECTRICAL RECORDS FOR LOGGED-IN MS
    // ============================================================

    List<ElectricalMaster> records =
            electricalMasterRepository
                    .findByUpdatedBy(userId);


    // ============================================================
    // NO DATA
    // ============================================================

    if (records == null
            || records.isEmpty()) {

        throw new IllegalArgumentException(
                "No electrical products found");
    }


    // ============================================================
    // RETURN FIRST RECORD
    // SAME AS PLUMBING IMPLEMENTATION
    // ============================================================

    return records.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
}
  

    @Override
    @Transactional
    public ElectricalMasterResponse update(
            String userIdStoreIdSku,
            ElectricalMasterRequest request) {

        // --------------------------------------------------------
        // PRIMARY KEY VALIDATION
        // --------------------------------------------------------

        if (userIdStoreIdSku == null
                || userIdStoreIdSku
                        .trim()
                        .isEmpty()) {

            throw new IllegalArgumentException(
                    "userIdStoreIdSku is required");
        }


        // --------------------------------------------------------
        // REQUEST VALIDATION
        // --------------------------------------------------------

        if (request == null) {

            throw new IllegalArgumentException(
                    "Request cannot be null");
        }


        // --------------------------------------------------------
        // PRODUCT SUB CATEGORY
        // --------------------------------------------------------

        if (request.getProductSubCategory() == null) {

            throw new IllegalArgumentException(
                    "Product sub category is required");
        }


        // --------------------------------------------------------
        // GET AUTHENTICATED SUPPLIER
        // --------------------------------------------------------

        String userId =
                getLoggedInMaterialSupplierUserId();


        // --------------------------------------------------------
        // FIND EXISTING RECORD
        // --------------------------------------------------------

        ElectricalMaster existing =
                electricalMasterRepository
                        .findByUserIdStoreIdSku(
                                userIdStoreIdSku.trim())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Electrical product not found: "
                                                + userIdStoreIdSku));


        // --------------------------------------------------------
        // OWNERSHIP CHECK
        // --------------------------------------------------------

        if (existing.getUpdatedBy() == null
                || !existing.getUpdatedBy()
                        .equals(userId)) {

            throw new IllegalArgumentException(
                    "You are not authorized to update "
                            + "this electrical product");
        }


        // ========================================================
        // STORE ID
        // ========================================================

        if (request.getStoreId() != null
                && !request.getStoreId()
                        .trim()
                        .isEmpty()) {

            String newStoreId =
                    request.getStoreId().trim();


            String existingStoreId = null;

            if (existing.getStore() != null) {

                existingStoreId =
                        existing.getStore()
                                .getStoreId();
            }


            if (existingStoreId == null) {

                throw new IllegalArgumentException(
                        "Existing electrical product has no store");
            }


            /*
             * Store ID is part of:
             *
             * userId_storeId_sku
             *
             * So it cannot be changed.
             */

            if (!newStoreId.equals(
                    existingStoreId)) {

                throw new IllegalArgumentException(
                        "Store ID cannot be changed because "
                                + "it is part of userIdStoreIdSku");
            }
        }


        // ========================================================
        // SKU
        // ========================================================

        String requestSku =
                request.getProductSubCategory()
                        .getSku();


        if (requestSku != null
                && !requestSku.trim().isEmpty()) {

            requestSku =
                    requestSku.trim();


            /*
             * SKU is part of:
             *
             * userId_storeId_sku
             *
             * So it cannot be changed.
             */

            if (!requestSku.equals(
                    existing.getSku())) {

                throw new IllegalArgumentException(
                        "SKU cannot be changed because "
                                + "it is part of userIdStoreIdSku");
            }
        }


        // ========================================================
        // PRODUCT NAME
        // ========================================================

        String productName =
                request.getProductSubCategory()
                        .getProductName();


        if (productName != null
                && !productName.trim().isEmpty()) {

            existing.setProductName(
                    productName.trim());
        }


        // ========================================================
        // PRODUCT DESCRIPTION
        // ========================================================

        String productDescription =
                request.getProductSubCategory()
                        .getProductDescription();


        if (productDescription != null) {

            existing.setProductDescription(
                    productDescription.trim());
        }


        // ========================================================
        // DIMENSIONS
        // ========================================================

        String dimensions =
                request.getProductSubCategory()
                        .getDimensions();


        if (dimensions != null) {

            existing.setDimensions(
                    dimensions.trim());
        }


        // ========================================================
        // CATEGORY
        // ========================================================

        existing.setProductCategory(
                "Electrical");


        // ========================================================
        // AUDIT
        // ========================================================

        existing.setUpdatedBy(
                userId);

        existing.setUpdatedDate(
                LocalDateTime.now());


        // ========================================================
        // SAVE
        // ========================================================

        ElectricalMaster updated =
                electricalMasterRepository.save(
                        existing);


        return convertToResponse(updated);
    }


    // ============================================================
    // GET FOR ALL USERS
    //
    // GET /api/electrical-master/all
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<ElectricalMasterResponse> getForAll() {

        List<ElectricalMaster> records =
                electricalMasterRepository.findAll();


        List<ElectricalMasterResponse> response =
                new ArrayList<>();


        if (records == null
                || records.isEmpty()) {

            return response;
        }


        for (ElectricalMaster entity : records) {

            response.add(
                    convertToResponse(entity));
        }


        return response;
    }


    // ============================================================
    // ENTITY -> RESPONSE
    // ============================================================

    private ElectricalMasterResponse convertToResponse(
            ElectricalMaster entity) {

        ElectricalMasterResponse response =
                new ElectricalMasterResponse();


        // --------------------------------------------------------
        // PRIMARY KEY
        // --------------------------------------------------------

        response.setUserIdStoreIdSku(
                entity.getUserIdStoreIdSku());


        // --------------------------------------------------------
        // STORE ID
        // --------------------------------------------------------

        if (entity.getStore() != null) {

            response.setStoreId(
                    entity.getStore()
                            .getStoreId());
        }


        // --------------------------------------------------------
        // PRODUCT CATEGORY
        // --------------------------------------------------------

        response.setProductCategory(
                entity.getProductCategory());


        // --------------------------------------------------------
        // PRODUCT SUB CATEGORY
        //
        // Response DTO contains ONE object.
        // --------------------------------------------------------

        ElectricalMasterResponse.ProductSubCategory
                subCategory =
                new ElectricalMasterResponse
                        .ProductSubCategory();


        subCategory.setSku(
                entity.getSku());


        subCategory.setProductName(
                entity.getProductName());


        subCategory.setProductDescription(
                entity.getProductDescription());


        subCategory.setDimensions(
                entity.getDimensions());


        response.setProductSubCategory(
                subCategory);


        // --------------------------------------------------------
        // AUDIT
        // --------------------------------------------------------

        response.setUpdatedBy(
                entity.getUpdatedBy());


        response.setUpdatedDate(
                entity.getUpdatedDate());


        return response;
    }
}
