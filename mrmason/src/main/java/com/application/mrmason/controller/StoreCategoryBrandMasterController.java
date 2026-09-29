package com.application.mrmason.controller;

import com.application.mrmason.dto.*;
import com.application.mrmason.service.StoreCategoryBrandMasterService;
import io.swagger.v3.oas.models.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StoreCategoryBrandMasterController {

    private final StoreCategoryBrandMasterService storeCategoryBrandMasterService;

    @PostMapping("store-category-brand/create")
    public ResponseEntity<GenericResponse<List<StoreCategoryBrandMasterResponseDto>>> createStoreCategoryBrandMaster(
            @RequestBody List<StoreCategoryBrandMasterRequestDto> requestDtoList) {

        log.info("Received request to create store category brand master records. Items count: {}",
                requestDtoList != null ? requestDtoList.size() : 0);

        try{
            List<StoreCategoryBrandMasterResponseDto> response =
                    storeCategoryBrandMasterService.createStoreCategoryBrandMaster(requestDtoList);
            GenericResponse<List<StoreCategoryBrandMasterResponseDto>> result = GenericResponse.<List<StoreCategoryBrandMasterResponseDto>>builder()
                    .data(response)
                    .success(true)
                    .message("Data registered successfully")
                    .build();
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception ex){
            GenericResponse<List<StoreCategoryBrandMasterResponseDto>> error = GenericResponse.<List<StoreCategoryBrandMasterResponseDto>>builder()
                    .data(null)
                    .success(false)
                    .message("Unable to create data " + ex.getMessage())
                    .build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("store-category-brand/get")
    public ResponseEntity<GenericResponse<List<StoreCategoryBrandMasterResponseDto>>>
                        getAllStores (String materialCategory, String subMaterialCategory, String brand, String updatedBy){
        try {
            List<StoreCategoryBrandMasterResponseDto> response = storeCategoryBrandMasterService
                    .getMyStoreCategoryBrandMaster(materialCategory, subMaterialCategory, brand, updatedBy);

            GenericResponse<List<StoreCategoryBrandMasterResponseDto>>
                    genericResponse = GenericResponse.<List<StoreCategoryBrandMasterResponseDto>>builder()
                    .data(response)
                    .message("Fetched store category successfully")
                    .success(true)
                    .build();
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            GenericResponse<List<StoreCategoryBrandMasterResponseDto>>
                    error = GenericResponse.<List<StoreCategoryBrandMasterResponseDto>>builder()
                    .data(null)
                    .message("Unable to fetch store category " + e.getMessage())
                    .success(false)
                    .build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("store-category-brand/update")
    public ResponseEntity<GenericResponse<Optional<StoreCategoryBrandMasterResponseDto>>>
        updateStore (@RequestBody StoreCategoryBrandMasterRequestDto dto){
        try {
            Optional<StoreCategoryBrandMasterResponseDto> response = storeCategoryBrandMasterService
                    .updateStore(dto);

            GenericResponse<Optional<StoreCategoryBrandMasterResponseDto>>
                    genericResponse = GenericResponse.<Optional<StoreCategoryBrandMasterResponseDto>>builder()
                    .data(response)
                    .message("Updated store category successfully")
                    .success(true)
                    .build();
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            GenericResponse<Optional<StoreCategoryBrandMasterResponseDto>>
                    error = GenericResponse.<Optional<StoreCategoryBrandMasterResponseDto>>builder()
                    .data(null)
                    .message("Unable to update store category " + e.getMessage())
                    .success(false)
                    .build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-store-location")
    public ResponseEntity<GenericResponse<StoreMasterLocationResponseDto>> getStoresLocation(StoreMasterLocationRequestDto dto){

        try {
            StoreMasterLocationResponseDto response = storeCategoryBrandMasterService.getStoreLocation(dto);

            GenericResponse<StoreMasterLocationResponseDto>
                    genericResponse = GenericResponse.<StoreMasterLocationResponseDto>builder()
                    .data(response)
                    .message("Fetched stores location successfully")
                    .success(true)
                    .build();
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            GenericResponse<StoreMasterLocationResponseDto>
                    error = GenericResponse.<StoreMasterLocationResponseDto>builder()
                    .data(null)
                    .message("Unable to fetch store location " + e.getMessage())
                    .success(false)
                    .build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
