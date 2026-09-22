package com.application.mrmason.repository;


import com.application.mrmason.dto.AdminRolesResponseDto;
import com.application.mrmason.entity.AdminRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdminRolesRepository extends JpaRepository<AdminRoles, String> {

    Optional<AdminRoles> findById(String id);

/*    @Query("SELECT ar FROM AdminRoles ar WHERE ar.updated=:updatedBy")
    public List<AdminRoles> findRolesByUpdatedBy(@Param("updatedBy") String updatedBy );*/
}
