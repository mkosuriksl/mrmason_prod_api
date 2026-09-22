package com.application.mrmason.dto;

import com.application.mrmason.entity.UserType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuperAdminResponseDto {

    @JsonProperty("super_admin_id")
    private String superAdminId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("mobile_number")
    private  String mobileNumber;

    @JsonProperty("is_verified")
    private Boolean isVerified;

    @JsonProperty("user_type")
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_date")
    private LocalDateTime updatedDate;
}
