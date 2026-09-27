package com.application.mrmason.service;


import com.application.mrmason.dto.SuperAdminLoginRequestDto;
import com.application.mrmason.dto.SuperAdminLoginResponseDto;
import com.application.mrmason.dto.SuperAdminRequestDto;
import com.application.mrmason.dto.SuperAdminResponseDto;

public interface SuperAdminService {

    public SuperAdminResponseDto createSuperAdmin(SuperAdminRequestDto superAdminRequestDto);

    public SuperAdminResponseDto getSuperAdmin(SuperAdminRequestDto superAdminRequestDto);

    public SuperAdminLoginResponseDto loginSuperAdmin(SuperAdminLoginRequestDto dto);
}
