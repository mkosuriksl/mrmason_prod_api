package com.application.mrmason.service.impl;

import com.application.mrmason.dto.AdminRolesRequestDto;
import com.application.mrmason.dto.AdminRolesResponseDto;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.AdminRoles;
import com.application.mrmason.entity.SuperAdmin;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.AdminRolesRepository;
import com.application.mrmason.repository.SuperAdminRepository;
import com.application.mrmason.service.AdminRolesServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminRolesServiceImpl implements AdminRolesServices {

    private final AdminRolesRepository adminRolesRepository;
    private final AdminDetailsRepo  adminDetailsRepo;
    private final SuperAdminRepository superAdminRepository;

    @Override
    public AdminRolesResponseDto createAdminRoles(AdminRolesRequestDto requestDto) {

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        String creatorId ;
        SuperAdmin superAdmin = superAdminRepository.findBySuperAdminEmail(currentEmail).orElse(null);
        if(superAdmin != null){
            creatorId = superAdmin.getSuperAdminId();
        }else{
            AdminDetails admin = adminDetailsRepo.findByEmail(currentEmail);
            if (admin != null) {
                creatorId = admin.getAdminId();
            } else {
                throw new RuntimeException("No authorized user found with email: " + currentEmail);
            }
        }

        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uniqueId = "AR" + dateTime;

        AdminRoles adminRoles = AdminRoles.builder()
                .roleId(uniqueId)
                .roleName(requestDto.getRoleName())
                .updatedBy(creatorId)
                .updatedDate(LocalDateTime.now())
                .build();

        AdminRoles savedRole = adminRolesRepository.save(adminRoles);

        return mapToDto(savedRole);
    }

    @Override
    public List<AdminRolesResponseDto> findRolesByUpdatedBy(String updatedBy) {
        return List.of();
    }

    public AdminRolesResponseDto mapToDto(AdminRoles adminRoles) {
        return AdminRolesResponseDto.builder()
                .roleId(adminRoles.getRoleId())
                .roleName(adminRoles.getRoleName())
                .updatedBy(adminRoles.getUpdatedBy())
                .updatedDate(adminRoles.getUpdatedDate())
                .build();
    }
}
