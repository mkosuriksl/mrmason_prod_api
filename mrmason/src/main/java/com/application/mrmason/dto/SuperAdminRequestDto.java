package com.application.mrmason.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuperAdminRequestDto {

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

    @JsonProperty("password")
    private String password;

    @JsonProperty("otp")
    private String otp;

    @JsonProperty("is_verified")
    private Boolean isVerified;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_date")
    private LocalDateTime updatedDate;
}
