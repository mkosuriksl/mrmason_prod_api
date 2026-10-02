
package com.application.mrmason.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialGroupGetDTO {

    private String materialCategory;

    private String materialSubCategory;

    private String brand;

    private List<MaterialGetDTO> materials;
}

