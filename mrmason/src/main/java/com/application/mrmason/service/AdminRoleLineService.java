package com.application.mrmason.service;

import com.application.mrmason.dto.AdminRoleLineRequestDto;
import com.application.mrmason.dto.AdminRoleLineResponseDto;
import com.application.mrmason.dto.AdminRolesRequestDto;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface AdminRoleLineService {

    AdminRoleLineResponseDto addRoleLine(AdminRoleLineRequestDto dto);

    List<AdminRoleLineResponseDto> getRoleLine();

    AdminRoleLineResponseDto updateRoleLine(AdminRoleLineRequestDto dto) throws AccessDeniedException;
}
