package com.application.mrmason.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminRoleLineResponseDto {

    @JsonProperty("line_id")
    private String lineId;

    @JsonProperty("roleId_lineId")
    private String roleIdLineId;

    @JsonProperty("role_id")
    private String roleId;

    @JsonProperty("role_line_name")
    private String roleLineName;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_date")
    private LocalDateTime updatedAt;
}
