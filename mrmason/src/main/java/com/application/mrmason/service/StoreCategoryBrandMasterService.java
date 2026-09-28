package com.application.mrmason.service;


import com.application.mrmason.dto.StoreCategoryBrandMasterRequestDto;
import com.application.mrmason.dto.StoreCategoryBrandMasterResponseDto;
import com.application.mrmason.entity.StoreCategoryBrandMaster;

import java.util.List;
import java.util.Optional;

public interface StoreCategoryBrandMasterService {

    List<StoreCategoryBrandMasterResponseDto> createStoreCategoryBrandMaster(List<StoreCategoryBrandMasterRequestDto> dtoList);

    List<StoreCategoryBrandMasterResponseDto> getMyStoreCategoryBrandMaster
            (String materialCategory, String subMaterialCategory, String brand, String updatedBy);

    Optional<StoreCategoryBrandMasterResponseDto> updateStore (StoreCategoryBrandMasterRequestDto dto);
}
