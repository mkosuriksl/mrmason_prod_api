package com.application.mrmason.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminRolesLine {

    @Id
    @Column(name="line_id")
    private String lineId;

    @Column(name = "roleId_lineId")
    private String roleIdLineId;

    @Column(name="role_id")
    private String roleId;

    @Column(name="role_line_name")
    private String roleLineName;

    @Column(name="updated_by")
    private String updatedBy;

    @Column(name="updated_date")
    private LocalDateTime updatedAt;
}
