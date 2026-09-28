package com.application.mrmason.service.impl;


import com.application.mrmason.dto.StoreCategoryBrandMasterRequestDto;
import com.application.mrmason.dto.StoreCategoryBrandMasterResponseDto;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.StoreCategoryBrandMaster;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.repository.StoreCategoryBrandMasterRepository;
import com.application.mrmason.service.StoreCategoryBrandMasterService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreCategoryBrandMasterServiceImpl implements StoreCategoryBrandMasterService {

    private final StoreCategoryBrandMasterRepository storeCategoryBrandMasterRepository;
    private final MaterialSupplierQuotationUserDAO materialSupplierQuotationUserDAO;
    private final AdminDetailsRepo adminDetailsRepo;

    @Override
    public List<StoreCategoryBrandMasterResponseDto> createStoreCategoryBrandMaster(List<StoreCategoryBrandMasterRequestDto> dtoList) {

        if(dtoList==null||dtoList.isEmpty()){
            return new ArrayList<>();
        }

        StoreCategoryBrandMasterRequestDto previousStore = dtoList.get(0);

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        String creatorId = null;
        MaterialSupplierQuotationUser materialSupplierId = materialSupplierQuotationUserDAO.findByEmail(currentEmail);
        if(materialSupplierId != null){
            creatorId = materialSupplierId.getBodSeqNo();
        }else{
            AdminDetails admin =  adminDetailsRepo.findByEmail(currentEmail);
            if(admin != null){
                creatorId = admin.getAdminId();
            }
        }

        if(creatorId == null){
            throw  new RuntimeException("Authorization failed: No user found for email " + currentEmail);
        }

        LocalDate currentDate = LocalDate.now();
        int currentCounter = fetchLatestStoreNumber();

        List<StoreCategoryBrandMaster> recordsToSave =   new ArrayList<>();

        for(StoreCategoryBrandMasterRequestDto dto : dtoList){

            String storeId = String.format("STR%04d", currentCounter);

                String primaryKey = String.format("%s_%s_%s_%s",
                        storeId,
                        dto.getMaterialCategory(),
                        dto.getSubMaterialCategory(),
                        dto.getBrand());

                StoreCategoryBrandMaster entity = StoreCategoryBrandMaster.builder()
                        .storeCategorySubMaterialCategoryBrand(primaryKey)
                        .storeId(storeId)
                        .materialCategory(dto.getMaterialCategory())
                        .subMaterialCategory(dto.getSubMaterialCategory())
                        .brand(dto.getBrand())
                        .updatedBy(creatorId)
                        .updatedDate(currentDate)
                        .build();
            recordsToSave.add(entity);
        }
        List<StoreCategoryBrandMaster> savedEntities = storeCategoryBrandMasterRepository.saveAll(recordsToSave);

        return mapToResponseDtoList(savedEntities);
    }

    private int fetchLatestStoreNumber() {
        return storeCategoryBrandMasterRepository.findByStoreId()
                .map(lastId -> {
                    try {
                        return Integer.parseInt(lastId.replace("STR", "").trim());
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .orElse(0);
    }


    private List<StoreCategoryBrandMasterResponseDto> mapToResponseDtoList (List<StoreCategoryBrandMaster> savedEntities) {

        if (savedEntities == null || savedEntities.isEmpty()) {
            return new ArrayList<>();
        }

        StoreCategoryBrandMaster firstRecord = savedEntities.get(0);

        List<StoreCategoryBrandMasterResponseDto.CategoryData> materialCategoryDtos = new ArrayList<>();

        for (StoreCategoryBrandMaster entity : savedEntities) {

            StoreCategoryBrandMasterResponseDto.BrandListDto brandDto = StoreCategoryBrandMasterResponseDto.BrandListDto
                    .builder()
                    .brand(entity.getBrand())
                    .build();

            StoreCategoryBrandMasterResponseDto.SubMaterialCategoryDto
                    subCatDto = StoreCategoryBrandMasterResponseDto.SubMaterialCategoryDto
                    .builder()
                    .subMaterialCategory(entity.getSubMaterialCategory())
                    .brands(List.of(brandDto))
                    .build();

            StoreCategoryBrandMasterResponseDto.CategoryData matCatDto =
                    StoreCategoryBrandMasterResponseDto.CategoryData.builder()
                            .storeCategorySubMaterialCategoryBrand(entity.getStoreCategorySubMaterialCategoryBrand())

                            .materialCategory(entity.getMaterialCategory())
                            .subMaterialCategories(List.of(subCatDto))
                            .build();

            materialCategoryDtos.add(matCatDto);
        }
            StoreCategoryBrandMasterResponseDto responseDto = StoreCategoryBrandMasterResponseDto.builder()
                    .storeId(firstRecord.getStoreId())
                    .updatedBy(firstRecord.getUpdatedBy())
                    .updatedDate(firstRecord.getUpdatedDate())
                    .data(materialCategoryDtos)
                    .build();

            return List.of(responseDto);
    }

    @Override
    public List<StoreCategoryBrandMasterResponseDto> getMyStoreCategoryBrandMaster(String materialCategory, String subMaterialCategory, String brand, String updatedBy) {

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        String creatorId = null;
        MaterialSupplierQuotationUser materialSupplierId = materialSupplierQuotationUserDAO.findByEmail(currentEmail);
        if(materialSupplierId != null){
            creatorId = materialSupplierId.getBodSeqNo();
        }else{
            AdminDetails admin =  adminDetailsRepo.findByEmail(currentEmail);
            if(admin != null){
                creatorId = admin.getAdminId();
            }
        }

        if (creatorId == null) {
            throw new RuntimeException("Authorization failed: No user found for email " + currentEmail);
        }

        String filterUpdatedBy = (updatedBy != null && !updatedBy.isEmpty()) ? updatedBy : creatorId;

        List<StoreCategoryBrandMaster> findAllStores = storeCategoryBrandMasterRepository.findByFilters(materialCategory, subMaterialCategory, brand, filterUpdatedBy);

        return mapToResponseDtoList(findAllStores);
    }

    @Override
    public Optional<StoreCategoryBrandMasterResponseDto> updateStore(StoreCategoryBrandMasterRequestDto dto) {

        if (dto == null) {
            throw new RuntimeException("Invalid request: Request body is empty");
        }
        if (dto.getStoreId() == null || dto.getStoreId().trim().isEmpty()) {
            throw new RuntimeException("Invalid request: store_id is required for update");
        }

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        String creatorId = null;
        MaterialSupplierQuotationUser materialSupplierId = materialSupplierQuotationUserDAO.findByEmail(currentEmail);
        if(materialSupplierId != null){
            creatorId = materialSupplierId.getBodSeqNo();
        }else{
            AdminDetails admin =  adminDetailsRepo.findByEmail(currentEmail);
            if(admin != null){
                creatorId = admin.getAdminId();
            }
        }

        if (creatorId == null) {
            throw new RuntimeException("Authorization failed: No user found for email " + currentEmail);
        }

        StoreCategoryBrandMaster entity = storeCategoryBrandMasterRepository.findByStoreId(dto.getStoreId())
                .orElseThrow(()-> new RuntimeException("Store id not found " + dto.getStoreId()));

        if (dto.getMaterialCategory() != null) entity.setMaterialCategory(dto.getMaterialCategory());
        if (dto.getSubMaterialCategory() != null) entity.setSubMaterialCategory(dto.getSubMaterialCategory());
        if (dto.getBrand() != null) entity.setBrand(dto.getBrand());

        entity.setUpdatedDate(LocalDate.now());
        entity.setUpdatedBy(creatorId);

        StoreCategoryBrandMaster savedEntity = storeCategoryBrandMasterRepository.save(entity);

        List<StoreCategoryBrandMasterResponseDto> dtoList = mapToResponseDtoList(List.of(savedEntity));

        return dtoList.isEmpty() ? Optional.empty() : Optional.of(dtoList.get(0));
    }
}
