
package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.application.mrmason.dto.MaterialGroupDTO;
import com.application.mrmason.dto.MaterialGroupGetDTO;
import com.application.mrmason.dto.MaterialGroupPageResponseDTO;
import com.application.mrmason.dto.ResponseModel;
import com.application.mrmason.entity.AdminMaterialMaster;
import com.application.mrmason.enums.RegSource;

public interface AdminMaterialMasterBasedOnCategoryService {

    // ============================================================
    // CREATE
    // ============================================================

   
List<MaterialGroupDTO> createAdminMaterialMaster(
        List<MaterialGroupDTO> requestGroups,
        RegSource regSource)
        throws AccessDeniedException;



    // ============================================================
    // UPDATE
    // ============================================================

    List<AdminMaterialMaster> updateAdminMaterialMasters(
            List<AdminMaterialMaster> updatedList,
            RegSource regSource)
            throws AccessDeniedException;

    // ============================================================
    // GET MATERIALS
    //
    // PUBLIC GET API
    // RETURNS GROUPED DATA
    // ============================================================

    MaterialGroupPageResponseDTO getAdminMaterialMaster(
            String materialCategory,
            String materialSubCategory,
            String brand,
            String modelNo,
            String size,
            String shape,
            String userId,
            Pageable pageable,
            Map<String, String> requestParams)
            throws AccessDeniedException;

    // ============================================================
    // UPLOAD IMAGES
    // ============================================================

    ResponseEntity<ResponseModel> uploadDoc(
            RegSource regSource,
            String skuId,
            MultipartFile materialMasterImage1,
            MultipartFile materialMasterImage2,
            MultipartFile materialMasterImage3,
            MultipartFile materialMasterImage4,
            MultipartFile materialMasterImage5)
            throws AccessDeniedException;

    // ============================================================
    // DISTINCT BRANDS
    //
    // PUBLIC GET API
    // ============================================================

    List<String> findDistinctBrandByMaterialCategory(
            String materialCategory,
            String materialSubCategory,
            Map<String, String> requestParams)
            throws AccessDeniedException;

    // ============================================================
    // DISTINCT CATEGORY + SUBCATEGORY
    //
    // PUBLIC GET API
    // ============================================================

    List<Map<String, Object>> findDistinctMaterialCategoryWithSubCategory()
            throws AccessDeniedException;

    // ============================================================
    // HOME SEARCH
    //
    // PUBLIC GET API
    // RETURNS GROUPED DATA
    // ============================================================

    List<MaterialGroupGetDTO> getMaterialsWithUserInfo(
            String materialCategory,
            String materialSubCategory,
            String brand,
            String location)
            throws AccessDeniedException;
}

