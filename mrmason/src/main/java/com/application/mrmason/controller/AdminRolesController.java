package com.application.mrmason.controller;

import com.application.mrmason.dto.AdminRolesRequestDto;
import com.application.mrmason.dto.AdminRolesResponseDto;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.service.AdminRolesServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/admin-roles")
@RequiredArgsConstructor
public class AdminRolesController {

    private final AdminRolesServices adminRolesServices;

    @PostMapping("/create-role")
    @PreAuthorize("hasAnyRole('SADM', 'Adm')")
    public ResponseEntity<GenericResponse<AdminRolesResponseDto>> createAdminRoles(
            @Valid @RequestBody AdminRolesRequestDto requestDto) {

        log.info("Received request to create admin role: {}", requestDto.getRoleName());

        try {
            AdminRolesResponseDto responseDto = adminRolesServices.createAdminRoles(requestDto);

            GenericResponse<AdminRolesResponseDto> response = GenericResponse.<AdminRolesResponseDto>builder()
                    .data(responseDto)
                    .success(true)
                    .message("Admin role created successfully.")
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException ex) {
            log.warn("Invalid request parameters for role creation: {}", ex.getMessage());

            GenericResponse<AdminRolesResponseDto> errorResponse = GenericResponse.<AdminRolesResponseDto>builder()
                    .data(null)
                    .success(false)
                    .message("Invalid input: " + ex.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception ex) {
            log.error("Failed to create admin role: {}", ex.getMessage(), ex);

            GenericResponse<AdminRolesResponseDto> errorResponse = GenericResponse.<AdminRolesResponseDto>builder()
                    .data(null)
                    .success(false)
                    .message("Unable to create admin role: " + ex.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<GenericResponse<List<AdminRolesResponseDto>>> getRoles(){

        try{
        List<AdminRolesResponseDto> list = adminRolesServices.findRolesByUpdatedBy();
            GenericResponse<List<AdminRolesResponseDto>> response = GenericResponse.<List<AdminRolesResponseDto>>builder()
                    .data(list)
                    .success(true)
                    .message("Fetched admin roles list successfully")
                    . build();
            return  ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid request parameters for role creation: {}", ex.getMessage());

            GenericResponse<List<AdminRolesResponseDto>> errorResponse = GenericResponse.<List<AdminRolesResponseDto>>builder()
                    .data(null)
                    .success(false)
                    .message("Invalid input: " + ex.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception ex) {
            log.error("Failed to create admin role: {}", ex.getMessage(), ex);

            GenericResponse<List<AdminRolesResponseDto>> errorResponse = GenericResponse.<List<AdminRolesResponseDto>>builder()
                    .data(null)
                    .success(false)
                    .message("Unable to create admin role: " + ex.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
