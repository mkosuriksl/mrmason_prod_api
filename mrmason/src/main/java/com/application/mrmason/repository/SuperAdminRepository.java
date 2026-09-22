package com.application.mrmason.repository;


import com.application.mrmason.entity.AdminRoles;
import com.application.mrmason.entity.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin, String> {

    @Query("SELECT sa FROM SuperAdmin sa WHERE sa.email=:email")
    Optional<SuperAdmin> findBySuperAdminEmail(@Param("email") String email);

    boolean existsByMobileNumber (String mobileNumber);

    boolean existsByEmail (String email);

}
