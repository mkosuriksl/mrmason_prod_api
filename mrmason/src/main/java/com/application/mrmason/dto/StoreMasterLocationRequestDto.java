package com.application.mrmason.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreMasterLocationRequestDto {

    @JsonProperty("material_category")
    private String materialCategory;

    @JsonProperty("sub_material_category")
    private String subMaterialCategory;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("storeId_userId")
    private String storeIdUserId;
}
