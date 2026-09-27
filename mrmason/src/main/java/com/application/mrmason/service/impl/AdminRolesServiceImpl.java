package com.application.mrmason.service.impl;

import com.application.mrmason.dto.AdminRolesRequestDto;
import com.application.mrmason.dto.AdminRolesResponseDto;
import com.application.mrmason.dto.UpdateAdminRolesDto;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.AdminRoles;
import com.application.mrmason.entity.SuperAdmin;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.AdminRolesRepository;
import com.application.mrmason.repository.SuperAdminRepository;
import com.application.mrmason.service.AdminRolesServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<AdminRolesResponseDto> findRolesByUpdatedBy() {

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        String creatorId ;
        SuperAdmin superAdmin = superAdminRepository.findBySuperAdminEmail(currentEmail).orElse(null);
        if(superAdmin != null){
            creatorId = superAdmin.getSuperAdminId();
        }else {
            AdminDetails admin = adminDetailsRepo.findByEmail(currentEmail);
            if (admin != null) {
                creatorId = admin.getAdminId();
            } else {
                throw new RuntimeException("No authorized user found with email: " + currentEmail);
            }
        }
            List<AdminRoles> adminRolesList = adminRolesRepository.findAll();

            List<AdminRolesResponseDto> roles =  adminRolesList.stream()
                    .filter(e -> e.getUpdatedBy().equals(creatorId))
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        return roles;
    }

    @Override
    public AdminRolesResponseDto updateAdminRoles(UpdateAdminRolesDto dto) throws AccessDeniedException {

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        String creatorId = resolveCreatorId(currentEmail);

        AdminRoles existingRole = adminRolesRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin role not found with name: " + dto.getRoleId()));

        if(!creatorId.equals(existingRole.getUpdatedBy())){
            throw new AccessDeniedException("Forbidden: You can only update roles that you created.");
        }
        existingRole.setRoleName(dto.getRoleName());
        existingRole.setUpdatedBy(creatorId);

        AdminRoles savedRole = adminRolesRepository.save(existingRole);
        return mapToDto(savedRole);
    }

    private String resolveCreatorId(String email) {
        return superAdminRepository.findBySuperAdminEmail(email)
                .map(SuperAdmin::getSuperAdminId)
                .orElseGet(() ->
                        Optional.ofNullable(adminDetailsRepo.findByEmail(email))
                                .map(AdminDetails::getAdminId)
                                .orElseThrow(() -> new RuntimeException("No authorized user found with email: " + email))
                );
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
