package com.application.mrmason.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class SuperAdmin {


    @Id
    @Column(name="super_admin_id")
    private String superAdminId;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="email")
    private String email;

    @Column(name="mobile_number", unique = true)
    private  String mobileNumber;

    @Column(name="password")
    private String password;

    @Column(name="otp")
    private String otp;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name="user_type")
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(name="updated_by")
    private String updatedBy;

    @Column(name="updated_date")
    private LocalDateTime updatedDate;
}
