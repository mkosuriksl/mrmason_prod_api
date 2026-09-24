package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import com.application.mrmason.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.application.mrmason.entity.AdminMaterialMaster;
import com.application.mrmason.enums.RegSource;

public interface AdminMaterialMasterService {

	List<MaterialGroupDTO> createAdminMaterialMaster(List<MaterialGroupDTO> requestGroups, RegSource regSource)
			throws AccessDeniedException;

	List<AdminMaterialMaster> updateAdminMaterialMasters(List<AdminMaterialMaster> updatedList, RegSource regSource)
			throws AccessDeniedException;

	 Page<AdminMaterialMasterResponseWithImageDto> getAdminMaterialMaster(
	        String materialCategory, String materialSubCategory,
	        String brand, String modelNo, String size, String shape,
	        String userId, Pageable pageable, Map<String, String> requestParams) throws AccessDeniedException;

	ResponseEntity<ResponseModel> uploadDoc(RegSource regSource, String skuId,
			MultipartFile materialMasterImage1, MultipartFile materialMasterImage2, MultipartFile materialMasterImage3,
			MultipartFile materialMasterImage4, MultipartFile materialMasterImage5) throws AccessDeniedException;

	List<String> findDistinctBrandByMaterialCategory(String materialCategory,String materialSubCategory, Map<String, String> requestParams);

	
	List<Map<String, Object>> findDistinctMaterialCategoryWithSubCategory();

	AdminMaterialMasterResponseDTO getMaterialsWithUserInfo(String materialCategory, String materialSubCategory,
			String brand, String location);

	List<String> listAllMaterialMaster();

	List<MaterialSearchResultDTO> searchMaterialMaster(String materialCategory, String materialSubCategory, String brand, String userInput);

	List<MaterialMasterProductResponseDto> getProductBrandAndSku();

	MaterialCategoryHierarchyDto getMaterialHierarchyByCategory(String category);


}
