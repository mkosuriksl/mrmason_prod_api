package com.application.mrmason.controller;

import com.application.mrmason.dto.*;
import com.application.mrmason.service.SuperAdminService;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/super-admin")
public class SuperAdminController {

    private  final SuperAdminService superAdminService;

    @PostMapping("/register")
    public ResponseEntity<GenericResponse<SuperAdminResponseDto>>
                            createSuperAdmin(@RequestBody SuperAdminRequestDto dto){
        try {
            SuperAdminResponseDto response = superAdminService.createSuperAdmin(dto);

            GenericResponse<SuperAdminResponseDto> successResponse = GenericResponse.<SuperAdminResponseDto>builder()
                    .data(response)
                    .success(true)
                    .message("Super Admin created successfully")
                    .build();
            return ResponseEntity.ok(successResponse);
        } catch (IllegalArgumentException ex) {
            GenericResponse<SuperAdminResponseDto> errorResponse = GenericResponse.<SuperAdminResponseDto>builder()
                    .data(null)
                    .success(false)
                    .message("Invalid input: " + ex.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception ex) {
            GenericResponse<SuperAdminResponseDto> errorResponse = GenericResponse.<SuperAdminResponseDto>builder()
                    .data(null)
                    .success(false)
                    .message("Unable to create super admin: " + ex.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<SuperAdminLoginResponseDto>> login(
            @Valid @RequestBody SuperAdminLoginRequestDto loginDto) {

        log.info("Received login request for email: {}", loginDto.getEmail());

        try {
            SuperAdminLoginResponseDto response = superAdminService.loginSuperAdmin(loginDto);

            GenericResponse<SuperAdminLoginResponseDto> successResponse = GenericResponse.<SuperAdminLoginResponseDto>builder()
                    .data(response)
                    .success(true)
                    .message("Logged in successfully")
                    .build();
            return ResponseEntity.ok(successResponse);
        } catch (IllegalArgumentException ex) {
            GenericResponse<SuperAdminLoginResponseDto> errorResponse = GenericResponse.<SuperAdminLoginResponseDto>builder()
                    .data(null)
                    .success(false)
                    .message("Invalid input: " + ex.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception ex) {
            GenericResponse<SuperAdminLoginResponseDto> errorResponse = GenericResponse.<SuperAdminLoginResponseDto>builder()
                    .data(null)
                    .success(false)
                    .message("Unable to login super admin: " + ex.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
