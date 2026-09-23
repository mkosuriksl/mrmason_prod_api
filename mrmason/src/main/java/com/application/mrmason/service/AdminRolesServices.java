package com.application.mrmason.service;

import com.application.mrmason.dto.AdminRolesRequestDto;
import com.application.mrmason.dto.AdminRolesResponseDto;
import com.application.mrmason.entity.AdminRoles;
import lombok.Data;

import java.util.List;
import java.util.Optional;


public interface AdminRolesServices {

    AdminRolesResponseDto createAdminRoles(AdminRolesRequestDto requestDto);

    List<AdminRolesResponseDto> findRolesByUpdatedBy();

    Optional<AdminRoles> updateAdminRoles(String roleName);


}
