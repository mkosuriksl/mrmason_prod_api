package com.application.mrmason.service;

import com.application.mrmason.dto.AdminRolesRequestDto;
import com.application.mrmason.dto.AdminRolesResponseDto;
import lombok.Data;

import java.util.List;


public interface AdminRolesServices {

    public AdminRolesResponseDto createAdminRoles(AdminRolesRequestDto requestDto);


    public List<AdminRolesResponseDto> findRolesByUpdatedBy(String updatedBy);
}
