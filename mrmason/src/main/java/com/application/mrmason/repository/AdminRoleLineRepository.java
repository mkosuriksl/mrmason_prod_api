package com.application.mrmason.repository;

import com.application.mrmason.dto.AdminRoleLineResponseDto;
import com.application.mrmason.entity.AdminRolesLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminRoleLineRepository extends JpaRepository<AdminRolesLine, String> {

    @Query("SELECT s FROM AdminRolesLine s WHERE s.updatedBy=:updatedBy")
    List<AdminRolesLine> findByUpdateBy(@Param("updatedBy") String updateBy);

}
