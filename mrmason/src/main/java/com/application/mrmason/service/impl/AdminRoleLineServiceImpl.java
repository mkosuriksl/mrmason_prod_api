package com.application.mrmason.service.impl;

import com.application.mrmason.dto.AdminRoleLineRequestDto;
import com.application.mrmason.dto.AdminRoleLineResponseDto;
import com.application.mrmason.dto.AdminRolesRequestDto;
import com.application.mrmason.dto.AdminRolesResponseDto;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.AdminRoles;
import com.application.mrmason.entity.AdminRolesLine;
import com.application.mrmason.entity.SuperAdmin;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.AdminRoleLineRepository;
import com.application.mrmason.repository.SuperAdminRepository;
import com.application.mrmason.service.AdminRoleLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AdminRoleLineServiceImpl implements AdminRoleLineService {

    private final AdminRoleLineRepository adminRoleLineRepository;
    private final SuperAdminRepository superAdminRepository;
    private final AdminDetailsRepo  adminDetailsRepo;

    @Override
    public AdminRoleLineResponseDto addRoleLine(AdminRoleLineRequestDto dto) {

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        String creatorId ;
        SuperAdmin superAdmin = superAdminRepository.findBySuperAdminEmail(currentEmail).orElse(null);
        if(superAdmin != null){
            creatorId = superAdmin.getSuperAdminId();
        }else {
            AdminDetails  admin = adminDetailsRepo.findByEmail(currentEmail);
            if (admin != null) {
                creatorId = admin.getAdminId();
            }else {
                throw new RuntimeException("No authorized user found with email: " + currentEmail);
            }
        }

        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uniqueId = "ARL"+ dateTime;


        AdminRolesLine  adminRolesLine = AdminRolesLine.builder()
                .lineId(uniqueId)
                .roleIdLineId(dto.getRoleIdLineId())
                .roleId(dto.getRoleId())
                .roleLineName(dto.getRoleLineName())
                .updatedBy(creatorId)
                .updatedAt(LocalDateTime.now())
                .build();

        AdminRolesLine saveLine = adminRoleLineRepository.save(adminRolesLine);

        return AdminRoleLineResponseDto
                .builder()
                .lineId(saveLine.getLineId())
                .roleIdLineId(saveLine.getRoleIdLineId())
                .roleId(saveLine.getRoleId())
                .roleLineName(saveLine.getRoleLineName())
                .updatedBy(saveLine.getUpdatedBy())
                .updatedAt(saveLine.getUpdatedAt())
                .build();
    }

    @Override
    public List<AdminRoleLineResponseDto> getRoleLine() {

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

        List<AdminRolesLine>  adminRolesLines = adminRoleLineRepository.findByUpdateBy(creatorId);
        return adminRolesLines.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AdminRoleLineResponseDto updateRoleLine(AdminRoleLineRequestDto dto) throws AccessDeniedException {

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        String creatorId = resolvedUser(currentEmail);

        AdminRolesLine existingRole = adminRoleLineRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin role line not found with name: " + creatorId));

        if(!creatorId.equals(existingRole.getUpdatedBy())){
            throw new AccessDeniedException("Forbidden: You can only update roles that you created.");
        }

        existingRole.setRoleId(dto.getRoleId());
        existingRole.setRoleLineName(dto.getRoleLineName());
        existingRole.setRoleId(dto.getRoleId());
        adminRoleLineRepository.save(existingRole);
        return mapToDto(existingRole);
    }

    public String resolvedUser (String email){
        return superAdminRepository.findBySuperAdminEmail(email)
                .map(SuperAdmin::getSuperAdminId)
                .orElseGet(()->
                        Optional.ofNullable(adminDetailsRepo.findByEmail(email))
                                .map(AdminDetails::getAdminId)
                                .orElseThrow(() -> new RuntimeException("No authorized user found with email: " + email)));

    }

    AdminRoleLineResponseDto mapToDto (AdminRolesLine adminRolesLine){
        return AdminRoleLineResponseDto.builder()
                .lineId(adminRolesLine.getLineId())
                .roleIdLineId(adminRolesLine.getRoleIdLineId())
                .roleId(adminRolesLine.getRoleId())
                .roleLineName(adminRolesLine.getRoleLineName())
                .updatedBy(adminRolesLine.getUpdatedBy())
                .updatedAt(adminRolesLine.getUpdatedAt())
                .build();
    }
}
