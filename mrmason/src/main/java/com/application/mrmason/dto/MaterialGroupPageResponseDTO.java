
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
public class MaterialGroupPageResponseDTO {

    private String message;

    private boolean status;

    private List<MaterialGroupGetDTO> materials;

    private int currentPage;

    private int pageSize;

    private long totalElements;

    private int totalPages;
}

