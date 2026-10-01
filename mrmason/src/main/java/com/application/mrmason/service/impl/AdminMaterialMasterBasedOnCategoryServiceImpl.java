package com.application.mrmason.service.impl;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.application.mrmason.config.AWSConfig;
import com.application.mrmason.dto.AdminDetailsDto;
import com.application.mrmason.dto.MaterialDTO;
import com.application.mrmason.dto.MaterialGetDTO;
import com.application.mrmason.dto.MaterialGroupDTO;
import com.application.mrmason.dto.MaterialGroupGetDTO;
import com.application.mrmason.dto.MaterialGroupPageResponseDTO;
import com.application.mrmason.dto.ResponseModel;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.AdminMaterialMaster;
import com.application.mrmason.entity.CementMaster;
import com.application.mrmason.entity.ElectricalMaster;
import com.application.mrmason.entity.MaterialMaster;
import com.application.mrmason.entity.PaintMaster;
import com.application.mrmason.entity.PlumbingMaster;
import com.application.mrmason.entity.SteelMaster;
import com.application.mrmason.entity.UploadAdminMaterialMaster;
import com.application.mrmason.entity.UploadMatericalMasterImages;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.AdminMaterialMasterRepository;
import com.application.mrmason.repository.CementMasterRepository;
import com.application.mrmason.repository.ElectricalMasterRepository;
import com.application.mrmason.repository.MaterialMasterRepository;
import com.application.mrmason.repository.PaintMasterRepo;
import com.application.mrmason.repository.PlumbingMasterRepository;
import com.application.mrmason.repository.SteelMasterRepository;
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

    // ============================================================
    // ADMIN REPOSITORY
    // ============================================================

    @Autowired
    private AdminDetailsRepo adminRepo;

    // ============================================================
    // ADMIN MATERIAL MASTER
    // ============================================================

    @Autowired
    private AdminMaterialMasterRepository adminMaterialMasterRepository;

    // ============================================================
    // COMMON MATERIAL MASTER
    // ============================================================

    @Autowired
    private MaterialMasterRepository materialMasterRepository;

    // ============================================================
    // CATEGORY MASTER REPOSITORIES
    // ============================================================

    @Autowired
    private CementMasterRepository cementMasterRepository;

    @Autowired
    private SteelMasterRepository steelMasterRepository;

    @Autowired
    private PaintMasterRepo paintMasterRepository;

    @Autowired
    private PlumbingMasterRepository plumbingMasterRepository;

    @Autowired
    private ElectricalMasterRepository electricalMasterRepository;

    // ============================================================
    // AWS
    // ============================================================

    @Autowired
    private AWSConfig awsConfig;

    // ============================================================
    // IMAGE REPOSITORIES
    // ============================================================

    @Autowired
    private UploadMatericalMasterImagesRepository
            uploadMatericalMasterImagesRepository;

    @Autowired
    private UploadAdminMaterialMasterRepository
            uploadAdminMaterialMasterRepository;

    // ============================================================
    // ENTITY MANAGER
    // ============================================================

    @PersistenceContext
    private EntityManager entityManager;

    // ============================================================
    // USER INFORMATION
    // ============================================================

    private static class UserInfo {

        private final String userId;
        private final String role;

        UserInfo(String userId, String role) {
            this.userId = userId;
            this.role = role;
        }
    }

    // ============================================================
    // CREATE ADMIN MATERIAL MASTER
    // ============================================================

// ============================================================
// CREATE ADMIN MATERIAL MASTER
//
// POST:
// /admin-material-master/add?regSource=MRMASON
//
// materialCategory and materialSubCategory come from
// MaterialGroupDTO request body.
// ============================================================

@Override
@Transactional(rollbackFor = Exception.class)
public List<MaterialGroupDTO> createAdminMaterialMaster(
        List<MaterialGroupDTO> requestGroups,
        RegSource regSource)
        throws AccessDeniedException {

    UserInfo userInfo = getLoggedInUserInfo();

    // ========================================================
    // VALIDATE REQUEST
    // ========================================================

    if (requestGroups == null || requestGroups.isEmpty()) {
        throw new IllegalArgumentException(
                "Material groups are required.");
    }

    // ========================================================
    // PROCESS EACH GROUP
    // ========================================================

    for (MaterialGroupDTO group : requestGroups) {

        if (group == null) {
            continue;
        }

        // ====================================================
        // CATEGORY FROM REQUEST BODY
        // ====================================================

        if (!StringUtils.hasText(
                group.getMaterialCategory())) {

            throw new IllegalArgumentException(
                    "materialCategory is required.");
        }

        // ====================================================
        // SUB CATEGORY FROM REQUEST BODY
        // ====================================================

        if (!StringUtils.hasText(
                group.getMaterialSubCategory())) {

            throw new IllegalArgumentException(
                    "materialSubCategory is required.");
        }

        // ====================================================
        // BRAND FROM REQUEST BODY
        // ====================================================

        if (!StringUtils.hasText(
                group.getBrand())) {

            throw new IllegalArgumentException(
                    "brand is required.");
        }

        String category =
                group.getMaterialCategory()
                        .trim()
                        .toUpperCase();

        String subCategory =
                group.getMaterialSubCategory()
                        .trim()
                        .toUpperCase();

        String brand =
                group.getBrand()
                        .trim()
                        .toUpperCase();

        // Normalize request body values
        group.setMaterialCategory(category);
        group.setMaterialSubCategory(subCategory);
        group.setBrand(brand);

        // ====================================================
        // MATERIALS VALIDATION
        // ====================================================

        if (group.getMaterials() == null
                || group.getMaterials().isEmpty()) {

            throw new IllegalArgumentException(
                    "materials are required for brand: "
                            + brand);
        }

        // ====================================================
        // PROCESS MATERIALS
        // ====================================================

        for (MaterialDTO material : group.getMaterials()) {

            if (material == null) {
                continue;
            }

            // =================================================
            // SKU VALIDATION
            // =================================================

            if (!StringUtils.hasText(
                    material.getSkuId())) {

                throw new IllegalArgumentException(
                        "SKU is required for brand: "
                                + brand);
            }

            String sku =
                    material.getSkuId().trim();

            // =================================================
            // FULL SKU
            //
            // ADMIN_ID_CATEGORY_SUBCATEGORY_BRAND_SKU
            // =================================================

            String fullSku =
                    userInfo.userId
                            + "_"
                            + category
                            + "_"
                            + subCategory
                            + "_"
                            + brand
                            + "_"
                            + sku;

            System.out.println(
                    "======================================");

            System.out.println(
                    "ADMIN MATERIAL CREATE / UPDATE");

            System.out.println(
                    "Admin ID = "
                            + userInfo.userId);

            System.out.println(
                    "Category = "
                            + category);

            System.out.println(
                    "Sub Category = "
                            + subCategory);

            System.out.println(
                    "Brand = "
                            + brand);

            System.out.println(
                    "SKU = "
                            + sku);

            System.out.println(
                    "Full SKU = "
                            + fullSku);

            System.out.println(
                    "======================================");

            // =================================================
            // MATERIAL MASTER
            // =================================================

            MaterialMaster materialEntity =
                    materialMasterRepository
                            .findByMsCatmsSubCatmsBrandSkuId(
                                    fullSku)
                            .orElseGet(
                                    MaterialMaster::new);

            materialEntity.setMsCatmsSubCatmsBrandSkuId(
                    fullSku);

            materialEntity.setMaterialCategory(
                    category);

            materialEntity.setMaterialSubCategory(
                    subCategory);

            materialEntity.setBrand(
                    brand);

            materialEntity.setSku(
                    sku);

            materialEntity.setModelNo(
                    material.getModelNo());

            materialEntity.setModelName(
                    material.getModelName());

            materialEntity.setShape(
                    material.getShape());

            materialEntity.setWidth(
                    material.getWidth());

            materialEntity.setLength(
                    material.getLength());

            materialEntity.setSize(
                    material.getSize());

            materialEntity.setThickness(
                    material.getThickness());

            materialEntity.setStatus(
                    "Active");

            materialEntity.setUserId(
                    userInfo.userId);

            materialEntity.setUpdatedBy(
                    userInfo.userId);

            materialEntity.setUpdatedDate(
                    LocalDateTime.now());

            MaterialMaster savedMaterial =
                    materialMasterRepository.save(
                            materialEntity);

            // =================================================
            // ADMIN MATERIAL MASTER
            // =================================================

            AdminMaterialMaster adminEntity =
                    adminMaterialMasterRepository
                            .findBySkuId(fullSku)
                            .orElseGet(
                                    AdminMaterialMaster::new);

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

            adminMaterialMasterRepository.save(
                    adminEntity);

            // =================================================
            // CATEGORY MASTER
            // =================================================

            saveCategoryMaster(
                    savedMaterial,
                    category);
        }
    }

    return requestGroups;
}



    // ============================================================
    // GET LOGGED-IN ADMIN
    // ============================================================

    private UserInfo getLoggedInUserInfo()
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
                        .map(
                                GrantedAuthority::getAuthority)
                        .filter(
                                StringUtils::hasText)
                        .map(
                                role -> role.replace(
                                        "ROLE_",
                                        ""))
                        .map(
                                String::trim)
                        .collect(
                                Collectors.toList());

        boolean isAdmin =
                roleNames.stream()
                        .anyMatch(
                                role ->
                                        "Adm".equalsIgnoreCase(
                                                role));

        if (!isAdmin) {

            throw new AccessDeniedException(
                    "Only Admin is allowed.");
        }

        AdminDetails admin =
                adminRepo
                        .findByEmailAndUserType(
                                email.trim(),
                                UserType.Adm)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Admin not found: "
                                                        + email));

        if (!StringUtils.hasText(
                admin.getAdminId())) {

            throw new ResourceNotFoundException(
                    "Admin ID not found: "
                            + email);
        }

        return new UserInfo(
                admin.getAdminId().trim(),
                "Adm");
    }

    // ============================================================
    // SAVE CATEGORY MASTER
    // ============================================================

    private void saveCategoryMaster(
            MaterialMaster material,
            String category) {

        if (material == null
                || !StringUtils.hasText(category)) {

            return;
        }

        String normalizedCategory =
                category.trim().toUpperCase();

        switch (normalizedCategory) {

            case "CEMENT":
                saveCementMaster(material);
                break;

            case "STEEL":
                saveSteelMaster(material);
                break;

            case "PAINT":
                savePaintMaster(
                        material,
                        material.getImage());
                break;

            case "PLUMBING":
                savePlumbingMaster(material);
                break;

            case "ELECTRICAL":
                saveElectricalMaster(material);
                break;

            default:
                break;
        }
    }

    // ============================================================
    // CEMENT MASTER
    // ============================================================

    private CementMaster saveCementMaster(
            MaterialMaster material) {

        String fullSku =
                material.getMsCatmsSubCatmsBrandSkuId();

        CementMaster cement =
                cementMasterRepository
                        .findById(fullSku)
                        .orElseGet(
                                CementMaster::new);

        BeanUtils.copyProperties(
                material,
                cement);

        cement.setMsCatmsSubCatmsBrandSkuId(
                fullSku);

        return cementMasterRepository.save(cement);
    }

    // ============================================================
    // STEEL MASTER
    // ============================================================

    private SteelMaster saveSteelMaster(
            MaterialMaster material) {

        String fullSku =
                material.getMsCatmsSubCatmsBrandSkuId();

        SteelMaster steel =
                steelMasterRepository
                        .findById(fullSku)
                        .orElseGet(
                                SteelMaster::new);

        BeanUtils.copyProperties(
                material,
                steel);

        steel.setMsCatmsSubCatmsBrandSkuId(
                fullSku);

        return steelMasterRepository.save(steel);
    }

    // ============================================================
    // PAINT MASTER
    // ============================================================

    private PaintMaster savePaintMaster(
            MaterialMaster material,
            String colorImage) {

        String fullSku =
                material.getMsCatmsSubCatmsBrandSkuId();

        PaintMaster paint =
                paintMasterRepository
                        .findById(fullSku)
                        .orElseGet(
                                PaintMaster::new);

        String oldImage =
                paint.getImage();

        BeanUtils.copyProperties(
                material,
                paint);

        paint.setMsCatmsSubCatmsBrandSkuId(
                fullSku);

        if (StringUtils.hasText(colorImage)) {

            paint.setImage(colorImage);

        } else if (StringUtils.hasText(oldImage)) {

            paint.setImage(oldImage);
        }

        return paintMasterRepository.save(paint);
    }

    // ============================================================
    // PLUMBING MASTER
    // ============================================================

    private PlumbingMaster savePlumbingMaster(
            MaterialMaster material) {

        String fullSku =
                material.getMsCatmsSubCatmsBrandSkuId();

        PlumbingMaster plumbing =
                plumbingMasterRepository
                        .findById(fullSku)
                        .orElseGet(
                                PlumbingMaster::new);

        BeanUtils.copyProperties(
                material,
                plumbing);

        plumbing.setMsCatmsSubCatmsBrandSkuId(
                fullSku);

        return plumbingMasterRepository.save(
                plumbing);
    }

    // ============================================================
    // ELECTRICAL MASTER
    // ============================================================

    private ElectricalMaster saveElectricalMaster(
            MaterialMaster material) {

        String fullSku =
                material.getMsCatmsSubCatmsBrandSkuId();

        ElectricalMaster electrical =
                electricalMasterRepository
                        .findById(fullSku)
                        .orElseGet(
                                ElectricalMaster::new);

        BeanUtils.copyProperties(
                material,
                electrical);

        electrical.setMsCatmsSubCatmsBrandSkuId(
                fullSku);

        return electricalMasterRepository.save(
                electrical);
    }

    // ============================================================
    // UPDATE ADMIN MATERIAL MASTERS
    // ============================================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<AdminMaterialMaster> updateAdminMaterialMasters(
            List<AdminMaterialMaster> updatedList,
            RegSource regSource)
            throws AccessDeniedException {

        UserInfo userInfo =
                getLoggedInUserInfo();

        if (updatedList == null
                || updatedList.isEmpty()) {

            return Collections.emptyList();
        }

        List<AdminMaterialMaster> savedMaterials =
                new ArrayList<>();

        for (AdminMaterialMaster material :
                updatedList) {

            if (material == null
                    || !StringUtils.hasText(
                            material.getSkuId())) {

                continue;
            }

            String skuId =
                    material.getSkuId().trim();

            AdminMaterialMaster existing =
                    adminMaterialMasterRepository
                            .findBySkuId(skuId)
                            .orElseThrow(
                                    () ->
                                            new ResourceNotFoundException(
                                                    "Admin material with SKU ID not found: "
                                                            + skuId));

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

            AdminMaterialMaster savedAdmin =
                    adminMaterialMasterRepository
                            .save(existing);

            savedMaterials.add(savedAdmin);

            // ====================================================
            // COMMON MATERIAL
            // ====================================================

            MaterialMaster commonMaterial =
                    materialMasterRepository
                            .findByMsCatmsSubCatmsBrandSkuId(
                                    skuId)
                            .orElse(null);

            if (commonMaterial != null) {

                if (StringUtils.hasText(
                        material.getModelNo())) {

                    commonMaterial.setModelNo(
                            material.getModelNo());
                }

                if (StringUtils.hasText(
                        material.getModelName())) {

                    commonMaterial.setModelName(
                            material.getModelName());
                }

                if (StringUtils.hasText(
                        material.getShape())) {

                    commonMaterial.setShape(
                            material.getShape());
                }

                if (material.getWidth() != null) {

                    commonMaterial.setWidth(
                            material.getWidth());
                }

                if (material.getLength() != null) {

                    commonMaterial.setLength(
                            material.getLength());
                }

                if (material.getSize() != null) {

                    commonMaterial.setSize(
                            material.getSize());
                }

                if (material.getThickness() != null) {

                    commonMaterial.setThickness(
                            material.getThickness());
                }

                if (StringUtils.hasText(
                        material.getStatus())) {

                    commonMaterial.setStatus(
                            material.getStatus());
                }

                commonMaterial.setUpdatedBy(
                        userInfo.userId);

                commonMaterial.setUpdatedDate(
                        LocalDateTime.now());

                materialMasterRepository.save(
                        commonMaterial);

                saveCategoryMaster(
                        commonMaterial,
                        commonMaterial
                                .getMaterialCategory());
            }
        }

        return savedMaterials;
    }

    // ============================================================
    // GET ADMIN MATERIAL MASTER
    //
    // PUBLIC GET API
    //
    // RESPONSE IS NOW GROUPED
    // ============================================================

    @Override
    public MaterialGroupPageResponseDTO getAdminMaterialMaster(
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

        if (pageable == null) {

            throw new IllegalArgumentException(
                    "Pageable is required.");
        }

        // ========================================================
        // VALID REQUEST PARAMETERS
        // ========================================================

        List<String> expectedParams =
                Arrays.asList(
                        "materialCategory",
                        "materialSubCategory",
                        "brand",
                        "modelNo",
                        "size",
                        "brandsize",
                        "shape",
                        "userId",
                        "page",
                        "sort");

        if (requestParams != null) {

            for (String paramName :
                    requestParams.keySet()) {

                if (!expectedParams.contains(
                        paramName)) {

                    throw new IllegalArgumentException(
                            "Unexpected parameter '"
                                    + paramName
                                    + "' is not allowed.");
                }
            }
        }

        // ========================================================
        // MAIN QUERY
        // ========================================================

        CriteriaBuilder cb =
                entityManager.getCriteriaBuilder();

        CriteriaQuery<MaterialMaster> query =
                cb.createQuery(
                        MaterialMaster.class);

        Root<MaterialMaster> root =
                query.from(MaterialMaster.class);

        List<Predicate> predicates =
                buildMaterialPredicates(
                        cb,
                        root,
                        materialCategory,
                        materialSubCategory,
                        brand,
                        modelNo,
                        size,
                        shape,
                        userId);

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

        // ========================================================
        // COUNT
        // ========================================================

        CriteriaQuery<Long> countQuery =
                cb.createQuery(Long.class);

        Root<MaterialMaster> countRoot =
                countQuery.from(
                        MaterialMaster.class);

        List<Predicate> countPredicates =
                buildMaterialPredicates(
                        cb,
                        countRoot,
                        materialCategory,
                        materialSubCategory,
                        brand,
                        modelNo,
                        size,
                        shape,
                        userId);

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
                        .createQuery(countQuery)
                        .getSingleResult();

        // ========================================================
        // EMPTY RESPONSE
        // ========================================================

        if (materials == null
                || materials.isEmpty()) {

            return MaterialGroupPageResponseDTO
                    .builder()
                    .message(
                            "Material Master details retrieved successfully.")
                    .status(true)
                    .materials(
                            Collections.emptyList())
                    .currentPage(
                            pageable.getPageNumber())
                    .pageSize(
                            pageable.getPageSize())
                    .totalElements(
                            total)
                    .totalPages(
                            calculateTotalPages(
                                    total,
                                    pageable.getPageSize()))
                    .build();
        }

        // ========================================================
        // GET SKU IDS
        // ========================================================

        List<String> skuIds =
                materials.stream()
                        .filter(
                                material ->
                                        material != null)
                        .map(
                                MaterialMaster::
                                        getMsCatmsSubCatmsBrandSkuId)
                        .filter(
                                StringUtils::hasText)
                        .map(
                                String::trim)
                        .distinct()
                        .collect(
                                Collectors.toList());

        if (skuIds.isEmpty()) {

            return MaterialGroupPageResponseDTO
                    .builder()
                    .message(
                            "Material Master details retrieved successfully.")
                    .status(true)
                    .materials(
                            Collections.emptyList())
                    .currentPage(
                            pageable.getPageNumber())
                    .pageSize(
                            pageable.getPageSize())
                    .totalElements(
                            total)
                    .totalPages(
                            calculateTotalPages(
                                    total,
                                    pageable.getPageSize()))
                    .build();
        }

        // ========================================================
        // ADMIN MATERIALS
        // ========================================================

        List<AdminMaterialMaster> adminMaterials =
                adminMaterialMasterRepository
                        .findAllById(skuIds);

        Map<String, AdminMaterialMaster> adminMap =
                buildAdminMaterialMap(
                        adminMaterials);

        // ========================================================
        // IMAGES
        // ========================================================

        List<UploadMatericalMasterImages> images =
                uploadMatericalMasterImagesRepository
                        .findAllById(skuIds);

        Map<String, UploadMatericalMasterImages> imageMap =
                buildImageMap(images);

        // ========================================================
        // GROUP MATERIALS
        // ========================================================

        List<MaterialGroupGetDTO> groupedMaterials =
                buildGroupedMaterialResponse(
                        materials,
                        adminMap,
                        imageMap);

        // ========================================================
        // FINAL PAGINATED RESPONSE
        // ========================================================

        return MaterialGroupPageResponseDTO
                .builder()
                .message(
                        "Material Master details retrieved successfully.")
                .status(true)
                .materials(
                        groupedMaterials)
                .currentPage(
                        pageable.getPageNumber())
                .pageSize(
                        pageable.getPageSize())
                .totalElements(
                        total)
                .totalPages(
                        calculateTotalPages(
                                total,
                                pageable.getPageSize()))
                .build();
    }

    // ============================================================
    // MATERIAL PREDICATES
    // ============================================================

    private List<Predicate> buildMaterialPredicates(
            CriteriaBuilder cb,
            Root<MaterialMaster> root,
            String materialCategory,
            String materialSubCategory,
            String brand,
            String modelNo,
            String size,
            String shape,
            String userId) {

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

        // ========================================================
        // SIZE
        // MaterialMaster.size is BigDecimal
        // ========================================================

        if (StringUtils.hasText(size)) {

            try {

                BigDecimal sizeValue =
                        new BigDecimal(
                                size.trim());

                predicates.add(
                        cb.equal(
                                root.get("size"),
                                sizeValue));

            } catch (NumberFormatException e) {

                throw new IllegalArgumentException(
                        "Invalid size value: "
                                + size);
            }
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

        return predicates;
    }

    // ============================================================
    // BUILD ADMIN MATERIAL MAP
    // ============================================================

    private Map<String, AdminMaterialMaster>
            buildAdminMaterialMap(
                    List<AdminMaterialMaster> adminMaterials) {

        if (adminMaterials == null
                || adminMaterials.isEmpty()) {

            return Collections.emptyMap();
        }

        return adminMaterials.stream()
                .filter(
                        item ->
                                item != null
                                        && StringUtils.hasText(
                                                item.getSkuId()))
                .collect(
                        Collectors.toMap(
                                item ->
                                        item.getSkuId()
                                                .trim(),
                                item -> item,
                                (first, second) ->
                                        second,
                                LinkedHashMap::new));
    }

    // ============================================================
    // BUILD IMAGE MAP
    // ============================================================

    private Map<String, UploadMatericalMasterImages>
            buildImageMap(
                    List<UploadMatericalMasterImages> images) {

        if (images == null
                || images.isEmpty()) {

            return Collections.emptyMap();
        }

        return images.stream()
                .filter(
                        image ->
                                image != null
                                        && StringUtils.hasText(
                                                image.getSkuId()))
                .collect(
                        Collectors.toMap(
                                image ->
                                        image.getSkuId()
                                                .trim(),
                                image -> image,
                                (first, second) ->
                                        second,
                                LinkedHashMap::new));
    }

    // ============================================================
    // GROUP MATERIAL RESPONSE
    //
    // THIS IS USED BY:
    //
    // 1. /get-materials
    // 2. /home-searching
    //
    // STRUCTURE:
    //
    // category
    //   -> subCategory
    //      -> brand
    //         -> materials[]
    // ============================================================

    private List<MaterialGroupGetDTO>
            buildGroupedMaterialResponse(
                    List<MaterialMaster> materials,
                    Map<String, AdminMaterialMaster> adminMap,
                    Map<String, UploadMatericalMasterImages> imageMap) {

        if (materials == null
                || materials.isEmpty()) {

            return Collections.emptyList();
        }

        if (adminMap == null
                || adminMap.isEmpty()) {

            return Collections.emptyList();
        }

        Map<String, MaterialGroupGetDTO> grouped =
                new LinkedHashMap<>();

        for (MaterialMaster material :
                materials) {

            if (material == null) {
                continue;
            }

            String sku =
                    material
                            .getMsCatmsSubCatmsBrandSkuId();

            if (!StringUtils.hasText(sku)) {
                continue;
            }

            sku = sku.trim();

            AdminMaterialMaster adminMaterial =
                    adminMap.get(sku);

            if (adminMaterial == null) {
                continue;
            }

            // ====================================================
            // ONLY ACTIVE ADMIN MATERIALS
            // ====================================================

            if (!StringUtils.hasText(
                    adminMaterial.getStatus())
                    || !"Active".equalsIgnoreCase(
                            adminMaterial.getStatus())) {

                continue;
            }

            String category =
                    material.getMaterialCategory();

            String subCategory =
                    material.getMaterialSubCategory();

            String brand =
                    material.getBrand();

            if (!StringUtils.hasText(category)
                    || !StringUtils.hasText(subCategory)
                    || !StringUtils.hasText(brand)) {

                continue;
            }

            category =
                    category.trim();

            subCategory =
                    subCategory.trim();

            brand =
                    brand.trim();

            // ====================================================
            // GROUP KEY
            // ====================================================

            String groupKey =
                    category.toUpperCase()
                            + "|"
                            + subCategory.toUpperCase()
                            + "|"
                            + brand.toUpperCase();

            MaterialGroupGetDTO group =
                    grouped.get(groupKey);

            if (group == null) {

                group =
                        MaterialGroupGetDTO
                                .builder()
                                .materialCategory(
                                        category)
                                .materialSubCategory(
                                        subCategory)
                                .brand(
                                        brand)
                                .materials(
                                        new ArrayList<>())
                                .build();

                grouped.put(
                        groupKey,
                        group);
            }

            // ====================================================
            // MATERIAL DTO
            // ====================================================

            MaterialGetDTO materialDto =
                    MaterialGetDTO
                            .builder()
                            .skuId(
                                    sku)
                            .modelNo(
                                    material.getModelNo())
                            .modelName(
                                    material.getModelName())
                            .shape(
                                    material.getShape())
                            .width(
                                    material.getWidth())
                            .length(
                                    material.getLength())
                            .size(
                                    material.getSize())
                            .thickness(
                                    material.getThickness())
                            .status(
                                    material.getStatus())
                            .updatedBy(
                                    material.getUpdatedBy())
                            .build();

            // ====================================================
            // IMAGES
            // ====================================================

            UploadMatericalMasterImages image =
                    imageMap != null
                            ? imageMap.get(sku)
                            : null;

            if (image != null) {

                materialDto.setMaterialMasterImage1(
                        getPresignedImageUrl(
                                image.getMaterialMasterImage1()));

                materialDto.setMaterialMasterImage2(
                        getPresignedImageUrl(
                                image.getMaterialMasterImage2()));

                materialDto.setMaterialMasterImage3(
                        getPresignedImageUrl(
                                image.getMaterialMasterImage3()));

                materialDto.setMaterialMasterImage4(
                        getPresignedImageUrl(
                                image.getMaterialMasterImage4()));

                materialDto.setMaterialMasterImage5(
                        getPresignedImageUrl(
                                image.getMaterialMasterImage5()));
            }

            group.getMaterials()
                    .add(materialDto);
        }

        return new ArrayList<>(
                grouped.values());
    }

    // ============================================================
    // TOTAL PAGES
    // ============================================================

    private int calculateTotalPages(
            long totalElements,
            int pageSize) {

        if (pageSize <= 0) {
            return 0;
        }

        return (int) Math.ceil(
                (double) totalElements
                        / pageSize);
    }

    // ============================================================
    // UPLOAD ADMIN MATERIAL IMAGES
    //
    // WRITE API - ADMIN ONLY
    // ============================================================

    @Override
    @Transactional(rollbackFor = Exception.class)
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
                getLoggedInUserInfo();

        ResponseModel response =
                new ResponseModel();

        // ========================================================
        // VALIDATE SKU
        // ========================================================

        if (!StringUtils.hasText(
                msCatmsSubCatmsBrandSkuId)) {

            response.setError("true");

            response.setMsg(
                    "Full SKU ID is required.");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.BAD_REQUEST);
        }

        String fullSku =
                msCatmsSubCatmsBrandSkuId.trim();

        // ========================================================
        // VALIDATE IMAGES
        // ========================================================

        boolean hasImage =
                isValidFile(materialMasterImage1)
                        || isValidFile(materialMasterImage2)
                        || isValidFile(materialMasterImage3)
                        || isValidFile(materialMasterImage4)
                        || isValidFile(materialMasterImage5);

        if (!hasImage) {

            response.setError("true");

            response.setMsg(
                    "At least one image is required.");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.BAD_REQUEST);
        }

        // ========================================================
        // MATERIAL MASTER
        // ========================================================

        MaterialMaster material =
                materialMasterRepository
                        .findByMsCatmsSubCatmsBrandSkuId(
                                fullSku)
                        .orElse(null);

        if (material == null) {

            response.setError("true");

            response.setMsg(
                    "Full SKU not found in MaterialMaster.");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND);
        }

        // ========================================================
        // ADMIN MATERIAL MASTER
        // ========================================================

        AdminMaterialMaster adminMaterial =
                adminMaterialMasterRepository
                        .findBySkuId(fullSku)
                        .orElse(null);

        if (adminMaterial == null) {

            response.setError("true");

            response.setMsg(
                    "Admin material master not found for the full SKU.");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND);
        }

        // ========================================================
        // S3 DIRECTORY
        // ========================================================

        String directoryPath =
                "adminMaterialMaster/"
                        + fullSku
                        + "/";

        // ========================================================
        // COMMON IMAGE ENTITY
        // ========================================================

        UploadMatericalMasterImages uploadEntity =
                uploadMatericalMasterImagesRepository
                        .findById(fullSku)
                        .orElseGet(
                                UploadMatericalMasterImages::new);

        uploadEntity.setSkuId(fullSku);

        uploadEntity.setUpdatedBy(
                userInfo.userId);

        uploadEntity.setUpdatedDate(
                new Date());

        // ========================================================
        // ADMIN IMAGE ENTITY
        // ========================================================

        UploadAdminMaterialMaster uploadAdminEntity =
                uploadAdminMaterialMasterRepository
                        .findById(fullSku)
                        .orElseGet(
                                UploadAdminMaterialMaster::new);

        uploadAdminEntity.setSkuId(fullSku);

        uploadAdminEntity.setUpdatedBy(
                userInfo.userId);

        uploadAdminEntity.setUpdatedDate(
                new Date());

        // ========================================================
        // IMAGE 1
        // ========================================================

        if (isValidFile(materialMasterImage1)) {

            String filename =
                    getSafeFileName(
                            materialMasterImage1);

            String path =
                    directoryPath + filename;

            String imagePath =
                    awsConfig.uploadFileToS3Bucket(
                            path,
                            materialMasterImage1);

            imagePath =
                    extractS3ObjectKey(imagePath);

            if (!StringUtils.hasText(imagePath)) {

                throw new IllegalStateException(
                        "Failed to upload image 1 to S3.");
            }

            uploadEntity.setMaterialMasterImage1(
                    imagePath);

            uploadAdminEntity.setMaterialMasterImage1(
                    imagePath);
        }

        // ========================================================
        // IMAGE 2
        // ========================================================

        if (isValidFile(materialMasterImage2)) {

            String filename =
                    getSafeFileName(
                            materialMasterImage2);

            String path =
                    directoryPath + filename;

            String imagePath =
                    awsConfig.uploadFileToS3Bucket(
                            path,
                            materialMasterImage2);

            imagePath =
                    extractS3ObjectKey(imagePath);

            if (!StringUtils.hasText(imagePath)) {

                throw new IllegalStateException(
                        "Failed to upload image 2 to S3.");
            }

            uploadEntity.setMaterialMasterImage2(
                    imagePath);

            uploadAdminEntity.setMaterialMasterImage2(
                    imagePath);
        }

        // ========================================================
        // IMAGE 3
        // ========================================================

        if (isValidFile(materialMasterImage3)) {

            String filename =
                    getSafeFileName(
                            materialMasterImage3);

            String path =
                    directoryPath + filename;

            String imagePath =
                    awsConfig.uploadFileToS3Bucket(
                            path,
                            materialMasterImage3);

            imagePath =
                    extractS3ObjectKey(imagePath);

            if (!StringUtils.hasText(imagePath)) {

                throw new IllegalStateException(
                        "Failed to upload image 3 to S3.");
            }

            uploadEntity.setMaterialMasterImage3(
                    imagePath);

            uploadAdminEntity.setMaterialMasterImage3(
                    imagePath);
        }

        // ========================================================
        // IMAGE 4
        // ========================================================

        if (isValidFile(materialMasterImage4)) {

            String filename =
                    getSafeFileName(
                            materialMasterImage4);

            String path =
                    directoryPath + filename;

            String imagePath =
                    awsConfig.uploadFileToS3Bucket(
                            path,
                            materialMasterImage4);

            imagePath =
                    extractS3ObjectKey(imagePath);

            if (!StringUtils.hasText(imagePath)) {

                throw new IllegalStateException(
                        "Failed to upload image 4 to S3.");
            }

            uploadEntity.setMaterialMasterImage4(
                    imagePath);

            uploadAdminEntity.setMaterialMasterImage4(
                    imagePath);
        }

        // ========================================================
        // IMAGE 5
        // ========================================================

        if (isValidFile(materialMasterImage5)) {

            String filename =
                    getSafeFileName(
                            materialMasterImage5);

            String path =
                    directoryPath + filename;

            String imagePath =
                    awsConfig.uploadFileToS3Bucket(
                            path,
                            materialMasterImage5);

            imagePath =
                    extractS3ObjectKey(imagePath);

            if (!StringUtils.hasText(imagePath)) {

                throw new IllegalStateException(
                        "Failed to upload image 5 to S3.");
            }

            uploadEntity.setMaterialMasterImage5(
                    imagePath);

            uploadAdminEntity.setMaterialMasterImage5(
                    imagePath);
        }

        // ========================================================
        // SAVE IMAGE ENTITIES
        // ========================================================

        uploadMatericalMasterImagesRepository.save(
                uploadEntity);

        uploadAdminMaterialMasterRepository.save(
                uploadAdminEntity);

        // ========================================================
        // UPDATE MATERIAL MASTER IMAGE
        // ========================================================

        String firstImage =
                getFirstImage(uploadEntity);

        if (StringUtils.hasText(firstImage)) {

            String firstImageKey =
                    extractS3ObjectKey(firstImage);

            material.setImage(
                    firstImageKey);

            material.setUpdatedBy(
                    userInfo.userId);

            material.setUpdatedDate(
                    LocalDateTime.now());

            materialMasterRepository.save(
                    material);

            saveCategoryMaster(
                    material,
                    material.getMaterialCategory());
        }

        // ========================================================
        // SUCCESS
        // ========================================================

        response.setError("false");

        response.setMsg(
                "Material images uploaded and stored successfully.");

        return new ResponseEntity<>(
                response,
                HttpStatus.OK);
    }

    // ============================================================
    // CHECK FILE
    // ============================================================

    private boolean isValidFile(
            MultipartFile file) {

        return file != null
                && !file.isEmpty();
    }

    // ============================================================
    // GET FIRST AVAILABLE IMAGE
    // ============================================================

    private String getFirstImage(
            UploadMatericalMasterImages uploadEntity) {

        if (uploadEntity == null) {
            return null;
        }

        if (StringUtils.hasText(
                uploadEntity.getMaterialMasterImage1())) {

            return uploadEntity
                    .getMaterialMasterImage1();
        }

        if (StringUtils.hasText(
                uploadEntity.getMaterialMasterImage2())) {

            return uploadEntity
                    .getMaterialMasterImage2();
        }

        if (StringUtils.hasText(
                uploadEntity.getMaterialMasterImage3())) {

            return uploadEntity
                    .getMaterialMasterImage3();
        }

        if (StringUtils.hasText(
                uploadEntity.getMaterialMasterImage4())) {

            return uploadEntity
                    .getMaterialMasterImage4();
        }

        if (StringUtils.hasText(
                uploadEntity.getMaterialMasterImage5())) {

            return uploadEntity
                    .getMaterialMasterImage5();
        }

        return null;
    }

    // ============================================================
    // EXTRACT S3 OBJECT KEY
    // ============================================================

    private String extractS3ObjectKey(
            String value) {

        if (!StringUtils.hasText(value)) {
            return null;
        }

        String trimmed =
                value.trim();

        String marker =
                ".amazonaws.com/";

        int markerIndex =
                trimmed.indexOf(marker);

        if (markerIndex >= 0) {

            String objectKey =
                    trimmed.substring(
                            markerIndex
                                    + marker.length());

            return StringUtils.hasText(
                    objectKey)
                    ? objectKey
                    : null;
        }

        if (trimmed.startsWith("http://")
                || trimmed.startsWith("https://")) {

            try {

                String encoded =
                        trimmed.replace(
                                " ",
                                "%20");

                URI uri =
                        URI.create(encoded);

                String path =
                        uri.getPath();

                if (StringUtils.hasText(path)) {

                    if (path.startsWith("/")) {

                        path =
                                path.substring(1);
                    }

                    return path.replace(
                            "%20",
                            " ");
                }

            } catch (Exception e) {

                System.err.println(
                        "Unable to extract S3 object key from URL: "
                                + trimmed);

                return null;
            }
        }

        return trimmed;
    }

    // ============================================================
    // GENERATE FRESH PRE-SIGNED IMAGE URL
    // ============================================================

    private String getPresignedImageUrl(
            String imagePath) {

        if (!StringUtils.hasText(imagePath)) {
            return null;
        }

        String objectKey =
                extractS3ObjectKey(
                        imagePath);

        if (!StringUtils.hasText(objectKey)) {
            return null;
        }

        return awsConfig.getUrl(
                objectKey);
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

        filename =
                filename.replace(
                        "..",
                        "_");

        if (!StringUtils.hasText(filename)) {

            filename =
                    "image_"
                            + System.currentTimeMillis();
        }

        return filename;
    }

    // ============================================================
    // DISTINCT BRANDS
    //
    // PUBLIC GET API
    // ============================================================

    @Override
    public List<String> findDistinctBrandByMaterialCategory(
            String materialCategory,
            String materialSubCategory,
            Map<String, String> requestParams)
            throws AccessDeniedException {

        List<String> expectedParams =
                Arrays.asList(
                        "materialCategory",
                        "materialSubCategory");

        if (requestParams != null) {

            for (String paramName :
                    requestParams.keySet()) {

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
    //
    // PUBLIC GET API
    // ============================================================

    @Override
    public List<Map<String, Object>>
            findDistinctMaterialCategoryWithSubCategory()
                    throws AccessDeniedException {

        List<Object[]> results =
                materialMasterRepository
                        .findCategoryAndSubCategory();

        if (results == null
                || results.isEmpty()) {

            return Collections.emptyList();
        }

        Map<String, Set<String>> grouped =
                new LinkedHashMap<>();

        for (Object[] row : results) {

            if (row == null
                    || row.length < 2) {

                continue;
            }

            String category =
                    row[0] != null
                            ? row[0].toString().trim()
                            : null;

            String subCategory =
                    row[1] != null
                            ? row[1].toString().trim()
                            : null;

            if (!StringUtils.hasText(category)) {
                continue;
            }

            grouped
                    .computeIfAbsent(
                            category,
                            k ->
                                    new LinkedHashSet<>())
                    .add(subCategory);
        }

        List<Map<String, Object>> response =
                new ArrayList<>();

        for (Map.Entry<String, Set<String>> entry :
                grouped.entrySet()) {

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
    // HOME SEARCHING
    //
    // PUBLIC GET API
    //
    // NOW RETURNS GROUPED DATA
    // ============================================================

    @Override
    public List<MaterialGroupGetDTO> getMaterialsWithUserInfo(
            String materialCategory,
            String materialSubCategory,
            String brand,
            String location)
            throws AccessDeniedException {

        List<MaterialMaster> materials =
                materialMasterRepository.searchMaterials(
                        materialCategory,
                        materialSubCategory,
                        brand);

        // ========================================================
        // NO MATERIALS
        // ========================================================

        if (materials == null
                || materials.isEmpty()) {

            return Collections.emptyList();
        }

        // ========================================================
        // GET SKU IDS
        // ========================================================

        List<String> skuIds =
                materials.stream()
                        .filter(
                                material ->
                                        material != null)
                        .map(
                                MaterialMaster::
                                        getMsCatmsSubCatmsBrandSkuId)
                        .filter(
                                StringUtils::hasText)
                        .map(
                                String::trim)
                        .distinct()
                        .collect(
                                Collectors.toList());

        if (skuIds.isEmpty()) {

            return Collections.emptyList();
        }

        // ========================================================
        // GET ADMIN MATERIALS
        // ========================================================

        List<AdminMaterialMaster> adminMaterials =
                adminMaterialMasterRepository
                        .findAllById(skuIds);

        Map<String, AdminMaterialMaster> adminMap =
                buildAdminMaterialMap(
                        adminMaterials);

        if (adminMap.isEmpty()) {

            return Collections.emptyList();
        }

        // ========================================================
        // GET IMAGES
        // ========================================================

        List<UploadMatericalMasterImages> images =
                uploadMatericalMasterImagesRepository
                        .findAllById(skuIds);

        Map<String, UploadMatericalMasterImages> imageMap =
                buildImageMap(images);

        // ========================================================
        // BUILD GROUPED RESPONSE
        // ========================================================

        return buildGroupedMaterialResponse(
                materials,
                adminMap,
                imageMap);
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


}