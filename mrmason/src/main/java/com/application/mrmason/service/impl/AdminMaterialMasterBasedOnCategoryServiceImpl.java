package com.application.mrmason.service.impl;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.application.mrmason.config.AWSConfig;
import com.application.mrmason.dto.AdminDetailsDto;
import com.application.mrmason.dto.AdminMaterialMasterResponseDTO;
import com.application.mrmason.dto.AdminMaterialMasterResponseWithImageDto;
import com.application.mrmason.dto.MaterialDTO;
import com.application.mrmason.dto.MaterialGroupDTO;
import com.application.mrmason.dto.MaterialSupplierDto;
import com.application.mrmason.dto.ResponseModel;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.AdminMaterialMaster;
import com.application.mrmason.entity.ElectricalMaster;
import com.application.mrmason.entity.MaterialMaster;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.PaintMaster;
import com.application.mrmason.entity.PlumbingMaster;
import com.application.mrmason.entity.StoreMaster;
import com.application.mrmason.entity.UploadAdminMaterialMaster;
import com.application.mrmason.entity.UploadMatericalMasterImages;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.AdminMaterialMasterRepository;
import com.application.mrmason.repository.ElectricalMasterRepository;
import com.application.mrmason.repository.MaterialMasterRepository;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.repository.PaintMasterRepo;
import com.application.mrmason.repository.PlumbingMasterRepository;
import com.application.mrmason.repository.StoreMasterRepository;
import com.application.mrmason.repository.UploadAdminMaterialMasterRepository;
import com.application.mrmason.repository.UploadMatericalMasterImagesRepository;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.AdminMaterialMasterBasedOnCategoryService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class AdminMaterialMasterBasedOnCategoryServiceImpl
        implements AdminMaterialMasterBasedOnCategoryService {

    @Autowired
    private AdminDetailsRepo adminRepo;

    @Autowired
    private AdminMaterialMasterRepository adminMaterialMasterRepository;

    @Autowired
    private MaterialMasterRepository materialMasterRepository;

    @Autowired
    private MaterialSupplierQuotationUserDAO materialSupplierQuotationUserDAO;

    @Autowired
    private PaintMasterRepo paintMasterRepository;

    @Autowired
    private PlumbingMasterRepository plumbingMasterRepository;

    // ============================================================
    // ELECTRICAL REPOSITORY
    // ============================================================

    @Autowired
    private ElectricalMasterRepository electricalMasterRepository;

    @Autowired
    private StoreMasterRepository storeMasterRepository;

    @Autowired
    private AWSConfig awsConfig;

    @Autowired
    private UploadMatericalMasterImagesRepository
            uploadMatericalMasterImagesRepository;

    @Autowired
    private UploadAdminMaterialMasterRepository
            uploadAdminMaterialMasterRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // ============================================================
    // USER INFORMATION
    // ============================================================

    private static class UserInfo {

        private final String userId;
        private final String role;

        UserInfo(
                String userId,
                String role) {

            this.userId = userId;
            this.role = role;
        }
    }

    // ============================================================
    // CREATE ADMIN MATERIAL MASTER
    // ONLY MATERIAL SUPPLIER (MS)
    // ============================================================

    @Override
    @Transactional
    public List<MaterialGroupDTO> createAdminMaterialMaster(
            List<MaterialGroupDTO> requestGroups,
            String materialCategory,
            String materialSubCategory,
            RegSource regSource,
            String storeId)
            throws AccessDeniedException {

        // --------------------------------------------------------
        // 1. Validate request
        // --------------------------------------------------------

        if (requestGroups == null
                || requestGroups.isEmpty()) {

            throw new IllegalArgumentException(
                    "Material groups are required.");
        }

        if (!StringUtils.hasText(materialCategory)) {

            throw new IllegalArgumentException(
                    "materialCategory is required.");
        }

        if (!StringUtils.hasText(materialSubCategory)) {

            throw new IllegalArgumentException(
                    "materialSubCategory is required.");
        }

        if (regSource == null) {

            throw new IllegalArgumentException(
                    "regSource is required.");
        }

        // --------------------------------------------------------
        // 2. Normalize category
        // --------------------------------------------------------

        String category =
                materialCategory.trim().toUpperCase();

        String subCategory =
                materialSubCategory.trim().toUpperCase();

        // --------------------------------------------------------
        // 3. Get logged-in MS
        // --------------------------------------------------------

        UserInfo userInfo =
                getLoggedInUserInfo(regSource);

        // --------------------------------------------------------
        // 4. PLUMBING / ELECTRICAL requires storeId
        // --------------------------------------------------------

        if (("PLUMBING".equalsIgnoreCase(category)
                || "ELECTRICAL".equalsIgnoreCase(category))
                && !StringUtils.hasText(storeId)) {

            throw new IllegalArgumentException(
                    "storeId is required for "
                            + category
                            + ".");
        }

        // --------------------------------------------------------
        // 5. Validate supplier store
        // --------------------------------------------------------

        if ("PLUMBING".equalsIgnoreCase(category)
                || "ELECTRICAL".equalsIgnoreCase(category)) {

            validateSupplierStore(
                    userInfo.userId,
                    storeId);
        }

        // --------------------------------------------------------
        // 6. Process groups
        // --------------------------------------------------------

        for (MaterialGroupDTO group : requestGroups) {

            if (group == null) {
                continue;
            }

            if (!StringUtils.hasText(group.getBrand())) {

                throw new IllegalArgumentException(
                        "brand is required.");
            }

            String brand =
                    group.getBrand().trim();

            group.setMaterialCategory(category);

            group.setMaterialSubCategory(subCategory);

            if (group.getMaterials() == null
                    || group.getMaterials().isEmpty()) {

                continue;
            }

            // ----------------------------------------------------
            // 7. Process each material
            // ----------------------------------------------------

            for (MaterialDTO material
                    : group.getMaterials()) {

                if (material == null) {
                    continue;
                }

                if (!StringUtils.hasText(
                        material.getSkuId())) {

                    throw new IllegalArgumentException(
                            "SKU is required.");
                }

                String sku =
                        material.getSkuId().trim();

                // ------------------------------------------------
                // Common SKU
                // ------------------------------------------------

                String shortSku =
                        buildShortSku(
                                category,
                                subCategory,
                                brand,
                                sku);

                // ------------------------------------------------
                // Supplier-specific SKU
                // ------------------------------------------------

                String fullSku =
                        userInfo.userId
                                + "_"
                                + shortSku;

                // =================================================
                // MATERIAL MASTER - COMMON TABLE
                // =================================================

                MaterialMaster materialEntity =
                        materialMasterRepository
                                .findByMsCatmsSubCatmsBrandSkuId(
                                        shortSku)
                                .orElse(null);

                if (materialEntity == null) {

                    materialEntity =
                            new MaterialMaster();

                    materialEntity
                            .setMsCatmsSubCatmsBrandSkuId(
                                    shortSku);

                    materialEntity
                            .setMaterialCategory(
                                    category);

                    materialEntity
                            .setMaterialSubCategory(
                                    subCategory);

                    materialEntity
                            .setBrand(
                                    brand);

                    materialEntity
                            .setSku(
                                    sku);

                    materialEntity
                            .setModelNo(
                                    material.getModelNo());

                    materialEntity
                            .setModelName(
                                    material.getModelName());

                    materialEntity
                            .setShape(
                                    material.getShape());

                    materialEntity
                            .setWidth(
                                    material.getWidth());

                    materialEntity
                            .setLength(
                                    material.getLength());

                    materialEntity
                            .setSize(
                                    material.getSize());

                    materialEntity
                            .setThickness(
                                    material.getThickness());

                    materialEntity
                            .setStatus(
                                    "Active");

                    materialEntity
                            .setUserId(
                                    userInfo.userId);

                    materialEntity
                            .setUpdatedBy(
                                    userInfo.userId);

                    materialEntity
                            .setUpdatedDate(
                                    LocalDateTime.now());

                    materialEntity =
                            materialMasterRepository
                                    .save(materialEntity);
                }

                // =================================================
                // ADMIN MATERIAL MASTER - SUPPLIER SPECIFIC
                // =================================================

                AdminMaterialMaster adminEntity =
                        adminMaterialMasterRepository
                                .findBySkuId(fullSku)
                                .orElse(null);

                if (adminEntity == null) {

                    adminEntity =
                            new AdminMaterialMaster();

                    adminEntity.setSkuId(fullSku);

                    adminEntity.setMaterialCategory(
                            category);

                    adminEntity.setMaterialSubCategory(
                            subCategory);

                    adminEntity.setBrand(
                            brand);

                    adminEntity.setModelNo(
                            material.getModelNo());

                    adminEntity.setModelName(
                            material.getModelName());

                    adminEntity.setShape(
                            material.getShape());

                    adminEntity.setWidth(
                            material.getWidth());

                    adminEntity.setLength(
                            material.getLength());

                    adminEntity.setSize(
                            material.getSize());

                    adminEntity.setThickness(
                            material.getThickness());

                    adminEntity.setStatus(
                            "Active");

                    adminEntity.setUpdatedBy(
                            userInfo.userId);

                    adminEntity.setUpdatedDate(
                            new Date());

                    adminMaterialMasterRepository
                            .save(adminEntity);
                }

                // =================================================
                // CATEGORY-SPECIFIC MASTER
                // =================================================

                if ("PAINT".equalsIgnoreCase(category)) {

                    savePaintMaster(
                            materialEntity,
                            materialEntity.getImage());

                } else if ("PLUMBING"
                        .equalsIgnoreCase(category)) {

                    savePlumbingMaster(
                            materialEntity,
                            userInfo.userId,
                            storeId);

                } else if ("ELECTRICAL"
                        .equalsIgnoreCase(category)) {

                    saveElectricalMaster(
                            materialEntity,
                            userInfo.userId,
                            storeId);
                }
            }
        }

        return requestGroups;
    }

    // ============================================================
    // BUILD SHORT SKU
    // ============================================================

    private String buildShortSku(
            String category,
            String subCategory,
            String brand,
            String sku) {

        return category.trim()
                + "_"
                + subCategory.trim()
                + "_"
                + brand.trim()
                + "_"
                + sku.trim();
    }

    // ============================================================
    // GET LOGGED-IN USER
    // ONLY MS
    // ============================================================

    private UserInfo getLoggedInUserInfo(
            RegSource regSource)
            throws AccessDeniedException {

        String email =
                AuthDetailsProvider.getLoggedEmail();

        if (!StringUtils.hasText(email)) {

            throw new ResourceNotFoundException(
                    "Logged-in user email not found.");
        }

        Collection<? extends GrantedAuthority>
                loggedInRole =
                AuthDetailsProvider.getLoggedRole();

        if (loggedInRole == null
                || loggedInRole.isEmpty()) {

            throw new AccessDeniedException(
                    "Logged-in user role not found.");
        }

        List<String> roleNames =
                loggedInRole.stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(StringUtils::hasText)
                        .map(role ->
                                role.replace(
                                        "ROLE_",
                                        ""))
                        .map(String::trim)
                        .collect(Collectors.toList());

        boolean isMaterialSupplier =
                roleNames.stream()
                        .anyMatch(role ->
                                "MS".equalsIgnoreCase(role));

        if (!isMaterialSupplier) {

            throw new AccessDeniedException(
                    "Only Material Supplier is allowed.");
        }

        if (regSource == null) {

            throw new IllegalArgumentException(
                    "regSource is required.");
        }

        MaterialSupplierQuotationUser supplier =
                materialSupplierQuotationUserDAO
                        .findByEmailAndUserTypeAndRegSource(
                                email.trim(),
                                UserType.MS,
                                regSource)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Material Supplier not found: "
                                                + email));

        if (!StringUtils.hasText(
                supplier.getBodSeqNo())) {

            throw new ResourceNotFoundException(
                    "BOD sequence number not found: "
                            + email);
        }

        return new UserInfo(
                supplier.getBodSeqNo(),
                "MS");
    }

    // ============================================================
    // VALIDATE SUPPLIER STORE
    // ============================================================

    private StoreMaster validateSupplierStore(
            String userId,
            String storeId)
            throws AccessDeniedException {

        if (!StringUtils.hasText(userId)) {

            throw new IllegalArgumentException(
                    "Supplier userId is required.");
        }

        if (!StringUtils.hasText(storeId)) {

            throw new IllegalArgumentException(
                    "storeId is required.");
        }

        StoreMaster store =
                storeMasterRepository
                        .findByStoreId(
                                storeId.trim())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Store not found: "
                                                + storeId));

        // --------------------------------------------------------
        // Store ownership
        // --------------------------------------------------------

        if (!userId.equals(
                store.getUpdatedBy())) {

            throw new AccessDeniedException(
                    "This store does not belong to the logged-in Material Supplier.");
        }

        return store;
    }

    // ============================================================
    // PAINT MASTER
    // ============================================================

    private PaintMaster savePaintMaster(
            MaterialMaster material,
            String colorImage) {

        if (material == null) {

            throw new IllegalArgumentException(
                    "Material is required for PAINT.");
        }

        if (!StringUtils.hasText(
                material.getModelNo())) {

            throw new IllegalArgumentException(
                    "Color code is required for PAINT. "
                            + "Please provide modelNo.");
        }

        int colorCode;

        try {

            colorCode =
                    Integer.parseInt(
                            material.getModelNo().trim());

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Invalid PAINT color code: "
                            + material.getModelNo()
                            + ". Color code must be numeric.");
        }

        PaintMaster paint =
                paintMasterRepository
                        .findById(colorCode)
                        .orElseGet(
                                PaintMaster::new);

        paint.setColorCode(
                colorCode);

        paint.setBrand(
                material.getBrand());

        paint.setWallType(
                material.getMaterialSubCategory());

        if (StringUtils.hasText(colorImage)) {

            paint.setColorImage(
                    colorImage);
        }

        return paintMasterRepository.save(
                paint);
    }

    // ============================================================
    // PLUMBING MASTER
    // ============================================================

    private PlumbingMaster savePlumbingMaster(
            MaterialMaster material,
            String userId,
            String storeId)
            throws AccessDeniedException {

        if (material == null) {

            throw new IllegalArgumentException(
                    "Material is required for PLUMBING.");
        }

        if (!StringUtils.hasText(userId)) {

            throw new IllegalArgumentException(
                    "Supplier userId is required.");
        }

        if (!StringUtils.hasText(storeId)) {

            throw new IllegalArgumentException(
                    "storeId is required for PLUMBING.");
        }

        if (!StringUtils.hasText(
                material.getSku())) {

            throw new IllegalArgumentException(
                    "SKU is required for PLUMBING.");
        }

        // --------------------------------------------------------
        // Validate store ownership
        // --------------------------------------------------------

        StoreMaster store =
                validateSupplierStore(
                        userId,
                        storeId);

        // --------------------------------------------------------
        // Plumbing key
        //
        // Example:
        // BOD123_19_70011505
        // --------------------------------------------------------

        String plumbingKey =
                userId
                        + "_"
                        + storeId.trim()
                        + "_"
                        + material.getSku().trim();

        // --------------------------------------------------------
        // Find existing plumbing record
        // --------------------------------------------------------

        Optional<PlumbingMaster> existing =
                plumbingMasterRepository
                        .findById(plumbingKey);

        PlumbingMaster plumbing;

        if (existing.isPresent()) {

            plumbing = existing.get();

        } else {

            plumbing =
                    new PlumbingMaster();

            plumbing.setUserIdStoreIdSku(
                    plumbingKey);
        }

        // --------------------------------------------------------
        // Set plumbing details
        // --------------------------------------------------------

        plumbing.setProductCategory(
                "Plumbing");

        plumbing.setSku(
                material.getSku());

        plumbing.setProductName(
                StringUtils.hasText(
                        material.getModelName())
                        ? material.getModelName()
                        : material.getSku());

        plumbing.setProductDescription(
                StringUtils.hasText(
                        material.getModelName())
                        ? material.getModelName()
                        : null);

        plumbing.setDimensions(
                buildDimensions(material));

        // --------------------------------------------------------
        // StoreMaster relationship
        // --------------------------------------------------------

        plumbing.setStore(store);

        // --------------------------------------------------------
        // Audit
        // --------------------------------------------------------

        plumbing.setUpdatedBy(
                userId);

        plumbing.setUpdatedDate(
                LocalDateTime.now());

        return plumbingMasterRepository.save(
                plumbing);
    }

    // ============================================================
    // ELECTRICAL MASTER
    // ============================================================

    private ElectricalMaster saveElectricalMaster(
            MaterialMaster material,
            String userId,
            String storeId)
            throws AccessDeniedException {

        if (material == null) {

            throw new IllegalArgumentException(
                    "Material is required for ELECTRICAL.");
        }

        if (!StringUtils.hasText(userId)) {

            throw new IllegalArgumentException(
                    "Supplier userId is required.");
        }

        if (!StringUtils.hasText(storeId)) {

            throw new IllegalArgumentException(
                    "storeId is required for ELECTRICAL.");
        }

        if (!StringUtils.hasText(
                material.getSku())) {

            throw new IllegalArgumentException(
                    "SKU is required for ELECTRICAL.");
        }

        // --------------------------------------------------------
        // Validate store ownership
        // --------------------------------------------------------

        StoreMaster store =
                validateSupplierStore(
                        userId,
                        storeId);

        // --------------------------------------------------------
        // Electrical key
        //
        // Example:
        // BOD123_19_80010001
        // --------------------------------------------------------

        String electricalKey =
                userId
                        + "_"
                        + storeId.trim()
                        + "_"
                        + material.getSku().trim();

        // --------------------------------------------------------
        // Find existing Electrical record
        // --------------------------------------------------------

        Optional<ElectricalMaster> existing =
                electricalMasterRepository
                        .findById(electricalKey);

        ElectricalMaster electrical;

        if (existing.isPresent()) {

            electrical = existing.get();

        } else {

            electrical =
                    new ElectricalMaster();

            electrical.setUserIdStoreIdSku(
                    electricalKey);
        }

        // --------------------------------------------------------
        // Set Electrical details
        // --------------------------------------------------------

        electrical.setProductCategory(
                "Electrical");

        electrical.setSku(
                material.getSku());

        electrical.setProductName(
                StringUtils.hasText(
                        material.getModelName())
                        ? material.getModelName()
                        : material.getSku());

        electrical.setProductDescription(
                StringUtils.hasText(
                        material.getModelName())
                        ? material.getModelName()
                        : null);

        electrical.setDimensions(
                buildDimensions(material));

        // --------------------------------------------------------
        // StoreMaster relationship
        // --------------------------------------------------------

        electrical.setStore(store);

        // --------------------------------------------------------
        // Audit
        // --------------------------------------------------------

        electrical.setUpdatedBy(
                userId);

        electrical.setUpdatedDate(
                LocalDateTime.now());

        return electricalMasterRepository.save(
                electrical);
    }

    // ============================================================
    // BUILD DIMENSIONS
    // ============================================================

    private String buildDimensions(
            MaterialMaster material) {

        if (material == null) {
            return null;
        }

        List<String> dimensions =
                new ArrayList<>();

        if (material.getWidth() != null) {

            dimensions.add(
                    "Width=" + material.getWidth());
        }

        if (material.getLength() != null) {

            dimensions.add(
                    "Length=" + material.getLength());
        }

        if (material.getSize() != null) {

            dimensions.add(
                    "Size=" + material.getSize());
        }

        if (material.getThickness() != null) {

            dimensions.add(
                    "Thickness=" + material.getThickness());
        }

        if (StringUtils.hasText(
                material.getShape())) {

            dimensions.add(
                    "Shape=" + material.getShape());
        }

        if (dimensions.isEmpty()) {
            return null;
        }

        return String.join(
                ", ",
                dimensions);
    }

    // ============================================================
    // UPDATE ADMIN MATERIAL MASTERS
    // PARTIAL UPDATE
    // ============================================================

    @Override
    @Transactional
    public List<AdminMaterialMaster> updateAdminMaterialMasters(
            List<AdminMaterialMaster> updatedList,
            RegSource regSource)
            throws AccessDeniedException {

        if (updatedList == null
                || updatedList.isEmpty()) {

            return Collections.emptyList();
        }

        UserInfo userInfo =
                getLoggedInUserInfo(regSource);

        List<AdminMaterialMaster> savedMaterials =
                new ArrayList<>();

        for (AdminMaterialMaster material
                : updatedList) {

            if (material == null
                    || !StringUtils.hasText(
                            material.getSkuId())) {

                continue;
            }

            AdminMaterialMaster existing =
                    adminMaterialMasterRepository
                            .findBySkuId(
                                    material.getSkuId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Material with SKU ID not found: "
                                                    + material.getSkuId()));

            if (!userInfo.userId.equals(
                    existing.getUpdatedBy())) {

                throw new AccessDeniedException(
                        "You are not allowed to update this material.");
            }

            if (StringUtils.hasText(
                    material.getModelNo())) {

                existing.setModelNo(
                        material.getModelNo());
            }

            if (StringUtils.hasText(
                    material.getModelName())) {

                existing.setModelName(
                        material.getModelName());
            }

            if (StringUtils.hasText(
                    material.getShape())) {

                existing.setShape(
                        material.getShape());
            }

            if (material.getWidth() != null) {

                existing.setWidth(
                        material.getWidth());
            }

            if (material.getLength() != null) {

                existing.setLength(
                        material.getLength());
            }

            if (material.getSize() != null) {

                existing.setSize(
                        material.getSize());
            }

            if (material.getThickness() != null) {

                existing.setThickness(
                        material.getThickness());
            }

            if (StringUtils.hasText(
                    material.getStatus())) {

                existing.setStatus(
                        material.getStatus());
            }

            existing.setUpdatedBy(
                    userInfo.userId);

            existing.setUpdatedDate(
                    new Date());

            savedMaterials.add(
                    adminMaterialMasterRepository
                            .save(existing));
        }

        return savedMaterials;
    }

    // ============================================================
    // GET ADMIN MATERIAL MASTER
    // ============================================================

    @Override
    public Page<AdminMaterialMasterResponseWithImageDto>
            getAdminMaterialMaster(
                    String materialCategory,
                    String materialSubCategory,
                    String brand,
                    String modelNo,
                    String size,
                    String shape,
                    String userId,
                    Pageable pageable,
                    Map<String, String> requestParams)
                    throws AccessDeniedException {

        List<String> expectedParams =
                Arrays.asList(
                        "materialCategory",
                        "materialSubCategory",
                        "brand",
                        "modelNo",
                        "size",
                        "shape",
                        "userId");

        if (requestParams != null) {

            for (String paramName
                    : requestParams.keySet()) {

                if (!expectedParams.contains(
                        paramName)) {

                    throw new IllegalArgumentException(
                            "Unexpected parameter '"
                                    + paramName
                                    + "' is not allowed.");
                }
            }
        }

        CriteriaBuilder cb =
                entityManager.getCriteriaBuilder();

        CriteriaQuery<MaterialMaster> query =
                cb.createQuery(
                        MaterialMaster.class);

        Root<MaterialMaster> root =
                query.from(
                        MaterialMaster.class);

        List<Predicate> predicates =
                new ArrayList<>();

        if (StringUtils.hasText(
                materialCategory)) {

            predicates.add(
                    cb.equal(
                            root.get(
                                    "materialCategory"),
                            materialCategory.trim()));
        }

        if (StringUtils.hasText(
                materialSubCategory)) {

            predicates.add(
                    cb.equal(
                            root.get(
                                    "materialSubCategory"),
                            materialSubCategory.trim()));
        }

        if (StringUtils.hasText(brand)) {

            predicates.add(
                    cb.equal(
                            root.get("brand"),
                            brand.trim()));
        }

        if (StringUtils.hasText(modelNo)) {

            predicates.add(
                    cb.equal(
                            root.get("modelNo"),
                            modelNo.trim()));
        }

        if (StringUtils.hasText(size)) {

            predicates.add(
                    cb.equal(
                            root.get("size"),
                            size.trim()));
        }

        if (StringUtils.hasText(shape)) {

            predicates.add(
                    cb.equal(
                            root.get("shape"),
                            shape.trim()));
        }

        if (StringUtils.hasText(userId)) {

            predicates.add(
                    cb.equal(
                            root.get("updatedBy"),
                            userId.trim()));
        }

        if (!predicates.isEmpty()) {

            query.where(
                    cb.and(
                            predicates.toArray(
                                    new Predicate[0])));
        }

        query.orderBy(
                cb.desc(
                        root.get("updatedDate")));

        TypedQuery<MaterialMaster> typedQuery =
                entityManager.createQuery(query);

        typedQuery.setFirstResult(
                (int) pageable.getOffset());

        typedQuery.setMaxResults(
                pageable.getPageSize());

        List<MaterialMaster> materials =
                typedQuery.getResultList();

        if (materials.isEmpty()) {

            return new PageImpl<>(
                    Collections.emptyList(),
                    pageable,
                    0);
        }

        // --------------------------------------------------------
        // Get image records
        // --------------------------------------------------------

        List<String> skuIds =
                materials.stream()
                        .map(
                                MaterialMaster::
                                        getMsCatmsSubCatmsBrandSkuId)
                        .filter(
                                StringUtils::hasText)
                        .collect(Collectors.toList());

        Map<String,
                UploadMatericalMasterImages> imageMap =
                new LinkedHashMap<>();

        if (!skuIds.isEmpty()) {

            CriteriaQuery<UploadMatericalMasterImages>
                    imageQuery =
                            cb.createQuery(
                                    UploadMatericalMasterImages.class);

            Root<UploadMatericalMasterImages>
                    imageRoot =
                            imageQuery.from(
                                    UploadMatericalMasterImages.class);

            imageQuery.select(
                    imageRoot)
                    .where(
                            imageRoot
                                    .get("skuId")
                                    .in(skuIds));

            List<UploadMatericalMasterImages>
                    images =
                    entityManager
                            .createQuery(
                                    imageQuery)
                            .getResultList();

            for (UploadMatericalMasterImages image
                    : images) {

                imageMap.put(
                        image.getSkuId(),
                        image);
            }
        }

        // --------------------------------------------------------
        // Merge material + images
        // --------------------------------------------------------

        List<AdminMaterialMasterResponseWithImageDto>
                mergedList =
                new ArrayList<>();

        for (MaterialMaster material
                : materials) {

            AdminMaterialMasterResponseWithImageDto dto =
                    new AdminMaterialMasterResponseWithImageDto();

            BeanUtils.copyProperties(
                    material,
                    dto);

            dto.setSkuId(
                    material
                            .getMsCatmsSubCatmsBrandSkuId());

            UploadMatericalMasterImages image =
                    imageMap.get(
                            material
                                    .getMsCatmsSubCatmsBrandSkuId());

            if (image != null) {

                dto.setMaterialMasterImage1(
                        image.getMaterialMasterImage1());

                dto.setMaterialMasterImage2(
                        image.getMaterialMasterImage2());

                dto.setMaterialMasterImage3(
                        image.getMaterialMasterImage3());

                dto.setMaterialMasterImage4(
                        image.getMaterialMasterImage4());

                dto.setMaterialMasterImage5(
                        image.getMaterialMasterImage5());
            }

            mergedList.add(dto);
        }

        // --------------------------------------------------------
        // Count query
        // --------------------------------------------------------

        CriteriaQuery<Long> countQuery =
                cb.createQuery(Long.class);

        Root<MaterialMaster> countRoot =
                countQuery.from(
                        MaterialMaster.class);

        List<Predicate> countPredicates =
                new ArrayList<>();

        if (StringUtils.hasText(
                materialCategory)) {

            countPredicates.add(
                    cb.equal(
                            countRoot.get(
                                    "materialCategory"),
                            materialCategory.trim()));
        }

        if (StringUtils.hasText(
                materialSubCategory)) {

            countPredicates.add(
                    cb.equal(
                            countRoot.get(
                                    "materialSubCategory"),
                            materialSubCategory.trim()));
        }

        if (StringUtils.hasText(brand)) {

            countPredicates.add(
                    cb.equal(
                            countRoot.get("brand"),
                            brand.trim()));
        }

        if (StringUtils.hasText(modelNo)) {

            countPredicates.add(
                    cb.equal(
                            countRoot.get("modelNo"),
                            modelNo.trim()));
        }

        if (StringUtils.hasText(size)) {

            countPredicates.add(
                    cb.equal(
                            countRoot.get("size"),
                            size.trim()));
        }

        if (StringUtils.hasText(shape)) {

            countPredicates.add(
                    cb.equal(
                            countRoot.get("shape"),
                            shape.trim()));
        }

        if (StringUtils.hasText(userId)) {

            countPredicates.add(
                    cb.equal(
                            countRoot.get("updatedBy"),
                            userId.trim()));
        }

        countQuery.select(
                cb.count(countRoot));

        if (!countPredicates.isEmpty()) {

            countQuery.where(
                    cb.and(
                            countPredicates.toArray(
                                    new Predicate[0])));
        }

        Long total =
                entityManager
                        .createQuery(
                                countQuery)
                        .getSingleResult();

        return new PageImpl<>(
                mergedList,
                pageable,
                total);
    }

    // ============================================================
    // UPLOAD MATERIAL IMAGES
    // ============================================================

    @Override
    @Transactional
    public ResponseEntity<ResponseModel> uploadDoc(
            RegSource regSource,
            String msCatmsSubCatmsBrandSkuId,
            MultipartFile materialMasterImage1,
            MultipartFile materialMasterImage2,
            MultipartFile materialMasterImage3,
            MultipartFile materialMasterImage4,
            MultipartFile materialMasterImage5)
            throws AccessDeniedException {

        UserInfo userInfo =
                getLoggedInUserInfo(regSource);

        ResponseModel response =
                new ResponseModel();

        if (!StringUtils.hasText(
                msCatmsSubCatmsBrandSkuId)) {

            response.setError("true");

            response.setMsg(
                    "SKU ID is required.");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.BAD_REQUEST);
        }

        MaterialMaster material =
                materialMasterRepository
                        .findByMsCatmsSubCatmsBrandSkuId(
                                msCatmsSubCatmsBrandSkuId)
                        .orElse(null);

        if (material == null) {

            response.setError("true");

            response.setMsg(
                    "SKU ID not found in MaterialMaster.");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND);
        }

        String fullSku =
                userInfo.userId
                        + "_"
                        + msCatmsSubCatmsBrandSkuId;

        AdminMaterialMaster adminMaterial =
                adminMaterialMasterRepository
                        .findBySkuId(fullSku)
                        .orElse(null);

        if (adminMaterial == null) {

            response.setError("true");

            response.setMsg(
                    "Material does not belong to the logged-in Material Supplier.");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.FORBIDDEN);
        }

        String directoryPath =
                "adminMaterialMaster/"
                        + msCatmsSubCatmsBrandSkuId
                        + "/";

        UploadMatericalMasterImages uploadEntity =
                uploadMatericalMasterImagesRepository
                        .findById(
                                msCatmsSubCatmsBrandSkuId)
                        .orElseGet(
                                UploadMatericalMasterImages::new);

        uploadEntity.setSkuId(
                msCatmsSubCatmsBrandSkuId);

        uploadEntity.setUpdatedBy(
                userInfo.userId);

        uploadEntity.setUpdatedDate(
                new Date());

        UploadAdminMaterialMaster uploadAdminEntity =
                uploadAdminMaterialMasterRepository
                        .findById(
                                fullSku)
                        .orElseGet(
                                UploadAdminMaterialMaster::new);

        uploadAdminEntity.setSkuId(
                fullSku);

        uploadAdminEntity.setUpdatedBy(
                userInfo.userId);

        uploadAdminEntity.setUpdatedDate(
                new Date());

        // --------------------------------------------------------
        // IMAGE 1
        // --------------------------------------------------------

        if (materialMasterImage1 != null
                && !materialMasterImage1.isEmpty()) {

            String filename =
                    getSafeFileName(
                            materialMasterImage1);

            String path =
                    directoryPath
                            + filename;

            String link =
                    awsConfig.uploadFileToS3Bucket(
                            path,
                            materialMasterImage1);

            uploadEntity.setMaterialMasterImage1(
                    link);

            uploadAdminEntity.setMaterialMasterImage1(
                    link);
        }

        // --------------------------------------------------------
        // IMAGE 2
        // --------------------------------------------------------

        if (materialMasterImage2 != null
                && !materialMasterImage2.isEmpty()) {

            String filename =
                    getSafeFileName(
                            materialMasterImage2);

            String path =
                    directoryPath
                            + filename;

            String link =
                    awsConfig.uploadFileToS3Bucket(
                            path,
                            materialMasterImage2);

            uploadEntity.setMaterialMasterImage2(
                    link);

            uploadAdminEntity.setMaterialMasterImage2(
                    link);
        }

        // --------------------------------------------------------
        // IMAGE 3
        // --------------------------------------------------------

        if (materialMasterImage3 != null
                && !materialMasterImage3.isEmpty()) {

            String filename =
                    getSafeFileName(
                            materialMasterImage3);

            String path =
                    directoryPath
                            + filename;

            String link =
                    awsConfig.uploadFileToS3Bucket(
                            path,
                            materialMasterImage3);

            uploadEntity.setMaterialMasterImage3(
                    link);

            uploadAdminEntity.setMaterialMasterImage3(
                    link);
        }

        // --------------------------------------------------------
        // IMAGE 4
        // --------------------------------------------------------

        if (materialMasterImage4 != null
                && !materialMasterImage4.isEmpty()) {

            String filename =
                    getSafeFileName(
                            materialMasterImage4);

            String path =
                    directoryPath
                            + filename;

            String link =
                    awsConfig.uploadFileToS3Bucket(
                            path,
                            materialMasterImage4);

            uploadEntity.setMaterialMasterImage4(
                    link);

            uploadAdminEntity.setMaterialMasterImage4(
                    link);
        }

        // --------------------------------------------------------
        // IMAGE 5
        // --------------------------------------------------------

        if (materialMasterImage5 != null
                && !materialMasterImage5.isEmpty()) {

            String filename =
                    getSafeFileName(
                            materialMasterImage5);

            String path =
                    directoryPath
                            + filename;

            String link =
                    awsConfig.uploadFileToS3Bucket(
                            path,
                            materialMasterImage5);

            uploadEntity.setMaterialMasterImage5(
                    link);

            uploadAdminEntity.setMaterialMasterImage5(
                    link);
        }

        // --------------------------------------------------------
        // Save common image record
        // --------------------------------------------------------

        uploadMatericalMasterImagesRepository
                .save(uploadEntity);

        // --------------------------------------------------------
        // Save supplier image record
        // --------------------------------------------------------

        uploadAdminMaterialMasterRepository
                .save(uploadAdminEntity);

        // --------------------------------------------------------
        // Update MaterialMaster image
        // --------------------------------------------------------

        String firstImage =
                uploadEntity
                        .getMaterialMasterImage1();

        if (StringUtils.hasText(firstImage)) {

            material.setImage(
                    firstImage);

            materialMasterRepository
                    .save(material);

            // --------------------------------------------
            // If PAINT, update PaintMaster image
            // --------------------------------------------

            if ("PAINT".equalsIgnoreCase(
                    material.getMaterialCategory())) {

                savePaintMaster(
                        material,
                        firstImage);
            }
        }

        response.setError("false");

        response.setMsg(
                "Material images uploaded and stored successfully.");

        return new ResponseEntity<>(
                response,
                HttpStatus.OK);
    }

    // ============================================================
    // SAFE FILE NAME
    // ============================================================

    private String getSafeFileName(
            MultipartFile file) {

        String filename =
                file.getOriginalFilename();

        if (!StringUtils.hasText(filename)) {

            filename =
                    "image_"
                            + System.currentTimeMillis();
        }

        filename =
                filename.replace(
                        "\\",
                        "/");

        int lastSlash =
                filename.lastIndexOf("/");

        if (lastSlash >= 0) {

            filename =
                    filename.substring(
                            lastSlash + 1);
        }

        return filename;
    }

    // ============================================================
    // DISTINCT BRANDS
    // ============================================================

    @Override
    public List<String> findDistinctBrandByMaterialCategory(
            String materialCategory,
            String materialSubCategory,
            Map<String, String> requestParams) {

        List<String> expectedParams =
                Arrays.asList(
                        "materialCategory",
                        "materialSubCategory");

        if (requestParams != null) {

            for (String paramName
                    : requestParams.keySet()) {

                if (!expectedParams.contains(
                        paramName)) {

                    throw new IllegalArgumentException(
                            "Unexpected parameter '"
                                    + paramName
                                    + "' is not allowed.");
                }
            }
        }

        return materialMasterRepository
                .findDistinctBrandByMaterialCategory(
                        materialCategory,
                        materialSubCategory);
    }

    // ============================================================
    // DISTINCT CATEGORY + SUBCATEGORY
    // ============================================================

    @Override
    public List<Map<String, Object>>
            findDistinctMaterialCategoryWithSubCategory() {

        List<Object[]> results =
                materialMasterRepository
                        .findCategoryAndSubCategory();

        Map<String, Set<String>> grouped =
                new LinkedHashMap<>();

        for (Object[] row : results) {

            if (row == null
                    || row.length < 2) {

                continue;
            }

            String category =
                    row[0] != null
                            ? row[0].toString()
                            : null;

            String subCategory =
                    row[1] != null
                            ? row[1].toString()
                            : null;

            if (!StringUtils.hasText(category)) {
                continue;
            }

            grouped
                    .computeIfAbsent(
                            category,
                            k -> new LinkedHashSet<>())
                    .add(subCategory);
        }

        List<Map<String, Object>> response =
                new ArrayList<>();

        for (Map.Entry<String, Set<String>> entry
                : grouped.entrySet()) {

            Map<String, Object> map =
                    new LinkedHashMap<>();

            map.put(
                    "category",
                    entry.getKey());

            map.put(
                    "subCategories",
                    new ArrayList<>(
                            entry.getValue()));

            response.add(map);
        }

        return response;
    }

    // ============================================================
    // GET MATERIALS WITH USER INFORMATION
    // ============================================================

    @Override
    public AdminMaterialMasterResponseDTO
            getMaterialsWithUserInfo(
                    String materialCategory,
                    String materialSubCategory,
                    String brand,
                    String location) {

        List<MaterialMaster> materials =
                materialMasterRepository
                        .searchMaterials(
                                materialCategory,
                                materialSubCategory,
                                brand);

        if (materials == null
                || materials.isEmpty()) {

            return new AdminMaterialMasterResponseDTO(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList());
        }

        List<AdminDetailsDto> adminDtos =
                new ArrayList<>();

        List<MaterialSupplierDto> supplierDtos =
                new ArrayList<>();

        Set<String> seenAdminIds =
                new HashSet<>();

        Set<String> seenSupplierIds =
                new HashSet<>();

        // --------------------------------------------------------
        // Fetch images
        // --------------------------------------------------------

        List<String> skuIds =
                materials.stream()
                        .map(
                                MaterialMaster::
                                        getMsCatmsSubCatmsBrandSkuId)
                        .filter(
                                StringUtils::hasText)
                        .collect(Collectors.toList());

        List<UploadMatericalMasterImages>
                images =
                uploadMatericalMasterImagesRepository
                        .findAllById(skuIds);

        Map<String,
                UploadMatericalMasterImages> imageMap =
                images.stream()
                        .filter(
                                image ->
                                        image != null
                                                && StringUtils
                                                        .hasText(
                                                                image.getSkuId()))
                        .collect(
                                Collectors.toMap(
                                        UploadMatericalMasterImages
                                                ::getSkuId,
                                        image -> image,
                                        (first, second) ->
                                                second));

        // --------------------------------------------------------
        // Material response
        // --------------------------------------------------------

        List<AdminMaterialMasterResponseWithImageDto>
                materialDtos =
                new ArrayList<>();

        for (MaterialMaster material
                : materials) {

            String userId =
                    material.getUpdatedBy();

            MaterialSupplierQuotationUser supplier =
                    null;

            if (StringUtils.hasText(userId)) {

                supplier =
                        materialSupplierQuotationUserDAO
                                .findByBodSeqNo(userId);
            }

            // ----------------------------------------------------
            // Location filter
            // ----------------------------------------------------

            if (StringUtils.hasText(location)) {

                if (supplier == null
                        || !location.trim()
                                .equalsIgnoreCase(
                                        supplier.getLocation())) {

                    continue;
                }
            }

            // ----------------------------------------------------
            // Material DTO
            // ----------------------------------------------------

            AdminMaterialMasterResponseWithImageDto dto =
                    new AdminMaterialMasterResponseWithImageDto();

            BeanUtils.copyProperties(
                    material,
                    dto);

            dto.setSkuId(
                    material
                            .getMsCatmsSubCatmsBrandSkuId());

            UploadMatericalMasterImages image =
                    imageMap.get(
                            material
                                    .getMsCatmsSubCatmsBrandSkuId());

            if (image != null) {

                dto.setMaterialMasterImage1(
                        image.getMaterialMasterImage1());

                dto.setMaterialMasterImage2(
                        image.getMaterialMasterImage2());

                dto.setMaterialMasterImage3(
                        image.getMaterialMasterImage3());

                dto.setMaterialMasterImage4(
                        image.getMaterialMasterImage4());

                dto.setMaterialMasterImage5(
                        image.getMaterialMasterImage5());
            }

            materialDtos.add(dto);

            // ----------------------------------------------------
            // Admin details
            // ----------------------------------------------------

            if (StringUtils.hasText(userId)) {

                AdminDetails user =
                        adminRepo.findByAdminId(
                                userId);

                if (user != null
                        && seenAdminIds.add(
                                user.getAdminId())) {

                    adminDtos.add(
                            toAdminDto(user));
                }
            }

            // ----------------------------------------------------
            // Supplier details
            // ----------------------------------------------------

            if (supplier != null
                    && StringUtils.hasText(
                            supplier.getBodSeqNo())
                    && seenSupplierIds.add(
                            supplier.getBodSeqNo())) {

                supplierDtos.add(
                        toSupplierDto(supplier));
            }
        }

        return new AdminMaterialMasterResponseDTO(
                materialDtos,
                adminDtos,
                supplierDtos);
    }

    // ============================================================
    // ADMIN DTO
    // ============================================================

    private AdminDetailsDto toAdminDto(
            AdminDetails admin) {

        if (admin == null) {
            return null;
        }

        AdminDetailsDto dto =
                new AdminDetailsDto();

        dto.setId(
                admin.getId());

        dto.setMobile(
                admin.getMobile());

        dto.setEmail(
                admin.getEmail());

        dto.setRegDate(
                admin.getRegDate());

        dto.setStatus(
                admin.getStatus());

        dto.setAdminId(
                admin.getAdminId());

        dto.setAdminName(
                admin.getAdminName());

        return dto;
    }

    // ============================================================
    // SUPPLIER DTO
    // ============================================================

    private MaterialSupplierDto toSupplierDto(
            MaterialSupplierQuotationUser supplier) {

        if (supplier == null) {
            return null;
        }

        MaterialSupplierDto dto =
                new MaterialSupplierDto();

        dto.setBodSeqNo(
                supplier.getBodSeqNo());

        dto.setName(
                supplier.getName());

        dto.setBusinessName(
                supplier.getBusinessName());

        dto.setMobile(
                supplier.getMobile());

        dto.setEmail(
                supplier.getEmail());

        dto.setAddress(
                supplier.getAddress());

        dto.setCity(
                supplier.getCity());

        dto.setDistrict(
                supplier.getDistrict());

        dto.setState(
                supplier.getState());

        dto.setLocation(
                supplier.getLocation());

        return dto;
    }
}