package com.application.mrmason.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import com.application.mrmason.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.application.mrmason.entity.AdminMaterialMaster;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.AdminMaterialMasterService;

@RestController
@RequestMapping("/admin-material-master")
public class AdminMaterialMasterController {

	@Autowired
	private AdminMaterialMasterService adminMaterialMasterService;

	@PostMapping("/add")
	public GenericResponse<List<MaterialGroupDTO>> createMaterials(@RequestBody List<MaterialGroupDTO> materialGroups,
			@RequestParam("regSource") RegSource regSource) throws AccessDeniedException {

		List<MaterialGroupDTO> savedMaterials = adminMaterialMasterService.createAdminMaterialMaster(materialGroups,
				regSource);

		return new GenericResponse<>("Materials saved successfully", true, savedMaterials);
	}

	@PutMapping("/update")
	public ResponseEntity<GenericResponse<List<AdminMaterialMaster>>> updateAdminMaterialMasters(
			@RequestBody AdminMaterialMasterRequestDTO requestDTO, @RequestParam("regSource") RegSource regSource)
			throws AccessDeniedException {

		List<AdminMaterialMaster> updatedMaterials = adminMaterialMasterService
				.updateAdminMaterialMasters(requestDTO.getMaterials(), regSource);
		GenericResponse<List<AdminMaterialMaster>> response = new GenericResponse<>(
				"Material Master Saved Successfully", true, updatedMaterials);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/get")
	public ResponseEntity<ResponseGetAdminMaterialMasterDto> getServiceRequestPaintQuotationService(
			@RequestParam(required = false) String materialCategory,
			@RequestParam(required = false) String materialSubCategory, @RequestParam(required = false) String brand,
			@RequestParam(required = false) String modelNo, @RequestParam(required = false) String brandsize,
			@RequestParam(required = false) String shape, @RequestParam(required = false) String userId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Map<String, String> requestParams)
			throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<AdminMaterialMasterResponseWithImageDto> srpqPage = adminMaterialMasterService.getAdminMaterialMaster(materialCategory,
				materialSubCategory, brand, modelNo, brandsize, shape, userId, pageable,requestParams);
		ResponseGetAdminMaterialMasterDto response = new ResponseGetAdminMaterialMasterDto();

		response.setMessage("Material Master details retrieved successfully.");
		response.setStatus(true);
		response.setGetAdminMaterialMaster(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("upload_images")
	public ResponseEntity<?> uploadCabDocs(@RequestParam("skuId") String skuId,
			@RequestParam("regSource") RegSource regSource,
			@RequestParam(value = "materialMasterImage1", required = false) MultipartFile materialMasterImage1,
			@RequestParam(value = "materialMasterImage2", required = false) MultipartFile materialMasterImage2,
			@RequestParam(value = "materialMasterImage3", required = false) MultipartFile materialMasterImage3,
			@RequestParam(value = "materialMasterImage4", required = false) MultipartFile materialMasterImage4,
			@RequestParam(value = "materialMasterImage5", required = false) MultipartFile materialMasterImage5)
			throws AccessDeniedException {
		return adminMaterialMasterService.uploadDoc(regSource, skuId, materialMasterImage1, materialMasterImage2,
				materialMasterImage3, materialMasterImage4, materialMasterImage5);
	}

	@GetMapping("/get-brand-by-materialcategory")
	public ResponseEntity<List<String>> getDistinctLocations(@RequestParam String materialCategory,
			@RequestParam String materialSubCategory,
			@RequestParam(required = false) Map<String, String> requestParams) {
		List<String> distinctLocations = adminMaterialMasterService
				.findDistinctBrandByMaterialCategory(materialCategory,materialSubCategory ,requestParams);

		if (distinctLocations.isEmpty()) {
			return ResponseEntity.noContent().build(); // Return 204 if no data found
		}

		return ResponseEntity.ok(distinctLocations); // Return 200 OK with the list of locations
	}

	@GetMapping("/distinct-material-category")
	public ResponseEntity<List<Map<String, Object>>> getDistinctMaterialCategory() {
	    List<Map<String, Object>> categories = adminMaterialMasterService.findDistinctMaterialCategoryWithSubCategory();
	    return ResponseEntity.ok(categories);
	}
//	public ResponseEntity<List<String>> getDistinctMaterialCategory() {
//		List<String> types = adminMaterialMasterService.findDistinctMaterialCategory();
//		return ResponseEntity.ok(types);
//	}

	@GetMapping("/home-search")
	public AdminMaterialMasterResponseDTO searchMaterials(@RequestParam(required = false) String materialCategory,
			@RequestParam(required = false) String materialSubCategory, @RequestParam(required = false) String brand,
			@RequestParam(required = false) String location) {
		return adminMaterialMasterService.getMaterialsWithUserInfo(materialCategory, materialSubCategory, brand,
				location);
	}

	@GetMapping("/get_material_category")
	public ResponseEntity<GenericResponse<List<String>>> getMaterialCategory() {
		try{
		List<String> materialList = adminMaterialMasterService.listAllMaterialMaster();
			GenericResponse<List<String>> response = GenericResponse.<List<String>>builder()
					.data(materialList)
					.success(true)
					.message("Successfully retrieved material category")
					.build();
			return ResponseEntity.ok(response);
		}catch (Exception e) {
			e.printStackTrace();
			GenericResponse<List<String>> errorResponse = GenericResponse.<List<String>>builder()
					.data(null)
					.success(false)
					.message("Unable to fetch material category "+ e.getMessage())
					.build();
			return ResponseEntity.ok(errorResponse);
		}
	}

	@GetMapping("/product_sku")
	public ResponseEntity<GenericResponse<List<MaterialSearchResultDTO>>> searchMaterialMaster(String materialCategory, String materialSubCategory, String brand, String userInput) {

		try {
			List<MaterialSearchResultDTO> response = adminMaterialMasterService.searchMaterialMaster(materialCategory, materialSubCategory, brand, userInput);

			GenericResponse<List<MaterialSearchResultDTO>> success = GenericResponse.<List<MaterialSearchResultDTO>>builder()
					.data(response)
					.success(true)
					.message("Fetched product name and sku successfully")
					.build();
			return ResponseEntity.ok(success);
		}catch (Exception e) {
			GenericResponse<List<MaterialSearchResultDTO>> error = GenericResponse.<List<MaterialSearchResultDTO>>builder()
					.data(null)
					.success(false)
					.message("Unable to fetch product name and sku " + e.getMessage())
					.build();
			return ResponseEntity.ok(error);
		}

	}
	@GetMapping("/get-product")
	public ResponseEntity<GenericResponse<List<MaterialMasterProductResponseDto>>> getProducts (){

		try{
			List<MaterialMasterProductResponseDto> response = adminMaterialMasterService.getProductBrandAndSku();
			GenericResponse<List<MaterialMasterProductResponseDto>> success = GenericResponse.<List<MaterialMasterProductResponseDto>>builder()
					.data(response)
					.success(true)
					.message("Fetched material data successfully")
					.build();
			return ResponseEntity.ok(success);
		}catch (Exception e) {
			GenericResponse<List<MaterialMasterProductResponseDto>> error = GenericResponse.<List<MaterialMasterProductResponseDto>>builder()
					.data(null)
					.success(false)
					.message("Unable to fetch material data " + e.getMessage())
					.build();
			return ResponseEntity.ok(error);
		}
	}
}
