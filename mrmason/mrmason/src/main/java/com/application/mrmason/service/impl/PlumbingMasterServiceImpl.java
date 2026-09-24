<<<<<<< HEAD
package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
=======

package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.mrmason.dto.PlumbingMasterRequest;
import com.application.mrmason.dto.PlumbingMasterResponse;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.PlumbingMaster;
import com.application.mrmason.entity.StoreMaster;
<<<<<<< HEAD
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.repository.PlumbingMasterRepository;
import com.application.mrmason.repository.StoreMasterRepository;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.PlumbingMasterService;

@Service
public class PlumbingMasterServiceImpl
        implements PlumbingMasterService {

    // ============================================================
    // REPOSITORIES
    // ============================================================

    @Autowired
    private PlumbingMasterRepository plumbingMasterRepository;

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
                                        + trimmedStoreId));
    }


    // ============================================================
    // CREATE PLUMBING MASTER
    //
    // POST
    // ONLY MS
    // ============================================================

    @Override
    @Transactional
    public PlumbingMasterResponse create(
            PlumbingMasterRequest request) {

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

            if (!"Plumbing".equalsIgnoreCase(
                    request.getProductCategory().trim())) {

                throw new IllegalArgumentException(
                        "Invalid product category. "
                                + "Expected Plumbing");
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
=======
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
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
        // --------------------------------------------------------

        String userIdStoreIdSku =
                userId
                        + "_"
<<<<<<< HEAD
                        + storeId
                        + "_"
                        + sku;


        // --------------------------------------------------------
        // DUPLICATE CHECK
        // --------------------------------------------------------

        if (plumbingMasterRepository
                .existsByUserIdStoreIdSku(
                        userIdStoreIdSku)) {

            throw new IllegalArgumentException(
                    "Plumbing product already exists: "
                            + userIdStoreIdSku);
        }


        // --------------------------------------------------------
        // CREATE ENTITY
        // --------------------------------------------------------

        PlumbingMaster plumbingMaster =
                new PlumbingMaster();


        // --------------------------------------------------------
        // PRIMARY KEY
        // --------------------------------------------------------

        plumbingMaster.setUserIdStoreIdSku(
                userIdStoreIdSku);


        // --------------------------------------------------------
        // PRODUCT CATEGORY
        // --------------------------------------------------------

        plumbingMaster.setProductCategory(
                "Plumbing");


        // --------------------------------------------------------
        // SKU
        // --------------------------------------------------------

        plumbingMaster.setSku(
                sku);


        // --------------------------------------------------------
        // PRODUCT NAME
        // --------------------------------------------------------

        plumbingMaster.setProductName(
                productName);


        // --------------------------------------------------------
        // PRODUCT DESCRIPTION
        // --------------------------------------------------------

        String productDescription =
                request.getProductSubCategory()
                        .getProductDescription();

        if (productDescription != null) {

            plumbingMaster.setProductDescription(
                    productDescription.trim());
        }


        // --------------------------------------------------------
        // DIMENSIONS
        // --------------------------------------------------------

        String dimensions =
                request.getProductSubCategory()
                        .getDimensions();

        if (dimensions != null) {

            plumbingMaster.setDimensions(
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

        plumbingMaster.setStore(store);


        // --------------------------------------------------------
        // AUDIT
        // --------------------------------------------------------

        plumbingMaster.setUpdatedBy(
                userId);

        plumbingMaster.setUpdatedDate(
                LocalDateTime.now());

=======
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
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528

        // --------------------------------------------------------
        // SAVE
        // --------------------------------------------------------

        PlumbingMaster saved =
                plumbingMasterRepository.save(
                        plumbingMaster);

<<<<<<< HEAD
=======
        // --------------------------------------------------------
        // RESPONSE
        // --------------------------------------------------------
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528

        return convertToResponse(saved);
    }

<<<<<<< HEAD

    // ============================================================
    // GET FOR LOGGED-IN MATERIAL SUPPLIER
    //
    // GET /api/plumbing-master
=======
    // ============================================================
    // GET BY ID
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
    // ============================================================

    @Override
    @Transactional(readOnly = true)
<<<<<<< HEAD
    public PlumbingMasterResponse getForMs(
            String storeId,
            String updatedBy,
            String productCategory,
            String productSubCategory) {

        // --------------------------------------------------------
        // GET AUTHENTICATED SUPPLIER
        // --------------------------------------------------------

        String userId =
                getLoggedInMaterialSupplierUserId();


        // --------------------------------------------------------
        // NEVER TRUST updatedBy FROM REQUEST
        // --------------------------------------------------------

        List<PlumbingMaster> records;


        // --------------------------------------------------------
        // STORE FILTER
        // --------------------------------------------------------

        if (storeId != null
                && !storeId.trim().isEmpty()) {

            records =
                    plumbingMasterRepository
                            .findByStore_StoreIdAndUpdatedBy(
                                    storeId.trim(),
                                    userId);

        } else {

            records =
                    plumbingMasterRepository
                            .findByUpdatedBy(
                                    userId);
        }


        // --------------------------------------------------------
        // PRODUCT CATEGORY VALIDATION
        // --------------------------------------------------------

        if (productCategory != null
                && !productCategory.trim().isEmpty()) {

            if (!"Plumbing".equalsIgnoreCase(
                    productCategory.trim())) {

                throw new IllegalArgumentException(
                        "Invalid product category. "
                                + "Expected Plumbing");
            }
        }


        // --------------------------------------------------------
        // PRODUCT SUB CATEGORY
        //
        // There is no separate productSubCategory column
        // in PlumbingMaster.
        //
        // The nested object contains:
        // SKU
        // productName
        // productDescription
        // dimensions
        //
        // Therefore no separate DB filter is applied here.
        // --------------------------------------------------------


        // --------------------------------------------------------
        // NO DATA
        // --------------------------------------------------------

        if (records == null
                || records.isEmpty()) {

            throw new IllegalArgumentException(
                    "No plumbing products found");
        }


        /*
         * IMPORTANT:
         *
         * PlumbingMasterResponse currently contains:
         *
         * ProductSubCategory productSubCategory;
         *
         * NOT:
         *
         * List<ProductSubCategory>
         *
         * Therefore this response can represent only ONE
         * product.
         *
         * If storeId is supplied, returning the first record
         * is normally the expected single-product response.
         */

        return convertToResponse(
                records.get(0));
    }


    // ============================================================
    // UPDATE PLUMBING MASTER
    //
    // PUT
    // ONLY MS
    // ============================================================

    @Override
    @Transactional
=======
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
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
    public PlumbingMasterResponse update(
            String userIdStoreIdSku,
            PlumbingMasterRequest request) {

        // --------------------------------------------------------
<<<<<<< HEAD
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
=======
        // REQUEST CANNOT BE NULL
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
        // --------------------------------------------------------

        if (request == null) {

<<<<<<< HEAD
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


=======
            throw new RuntimeException(
                    "Request cannot be null");
        }

>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
        // --------------------------------------------------------
        // FIND EXISTING RECORD
        // --------------------------------------------------------

<<<<<<< HEAD
        PlumbingMaster existing =
                plumbingMasterRepository
                        .findByUserIdStoreIdSku(
                                userIdStoreIdSku.trim())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Plumbing product not found: "
                                                + userIdStoreIdSku));


        // --------------------------------------------------------
        // OWNERSHIP CHECK
        // --------------------------------------------------------

        if (existing.getUpdatedBy() == null
                || !existing.getUpdatedBy()
                        .equals(userId)) {

            throw new IllegalArgumentException(
                    "You are not authorized to update "
                            + "this plumbing product");
        }


        // ========================================================
        // STORE ID
=======
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
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
        // ========================================================

        if (request.getStoreId() != null
                && !request.getStoreId()
                        .trim()
                        .isEmpty()) {

<<<<<<< HEAD
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
                        "Existing plumbing product has no store");
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
                "Plumbing");


        // ========================================================
        // AUDIT
        // ========================================================

        existing.setUpdatedBy(
                userId);

        existing.setUpdatedDate(
                LocalDateTime.now());


=======
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

>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
        // ========================================================
        // SAVE
        // ========================================================

        PlumbingMaster updated =
                plumbingMasterRepository.save(
<<<<<<< HEAD
                        existing);

=======
                        plumbingMaster);

        // ========================================================
        // RESPONSE
        // ========================================================
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528

        return convertToResponse(updated);
    }

<<<<<<< HEAD

    // ============================================================
    // GET FOR ALL USERS
    //
    // GET /api/plumbing-master/all
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<PlumbingMasterResponse> getForAll() {

        List<PlumbingMaster> records =
                plumbingMasterRepository.findAll();


        List<PlumbingMasterResponse> response =
                new ArrayList<>();


        if (records == null
                || records.isEmpty()) {

            return response;
        }


        for (PlumbingMaster entity : records) {

            response.add(
                    convertToResponse(entity));
        }


        return response;
    }

=======
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
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528

    // ============================================================
    // ENTITY -> RESPONSE
    // ============================================================

    private PlumbingMasterResponse convertToResponse(
            PlumbingMaster entity) {

<<<<<<< HEAD
        PlumbingMasterResponse response =
                new PlumbingMasterResponse();


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

        PlumbingMasterResponse.ProductSubCategory
                subCategory =
                new PlumbingMasterResponse
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
=======
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
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
