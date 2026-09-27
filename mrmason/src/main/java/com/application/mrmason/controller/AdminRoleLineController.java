package com.application.mrmason.controller;

import com.application.mrmason.dto.AdminRoleLineRequestDto;
import com.application.mrmason.dto.AdminRoleLineResponseDto;
import com.application.mrmason.dto.AdminRolesResponseDto;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.service.AdminRoleLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/role-line")
public class AdminRoleLineController {

    private final AdminRoleLineService adminRoleLineService;

    @PostMapping("/create")
    public ResponseEntity<GenericResponse<AdminRoleLineResponseDto>> createAdminRoleLine(@RequestBody AdminRoleLineRequestDto dto) {

        try{
            AdminRoleLineResponseDto response = adminRoleLineService.addRoleLine(dto);
            GenericResponse<AdminRoleLineResponseDto> result = GenericResponse.<AdminRoleLineResponseDto>builder()
                    .data(response)
                    .success(true)
                    .message("Admin role line added successfully")
                    .build();

            return ResponseEntity.ok(result);
        }catch (Exception e){
            GenericResponse<AdminRoleLineResponseDto> error = GenericResponse.<AdminRoleLineResponseDto>builder()
                    .data(null)
                    .success(false)
                    .message("Unable to add admin role line " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(error);
        }
    }


    @GetMapping("/get")
    public ResponseEntity<GenericResponse<List<AdminRoleLineResponseDto>>> getAdminRoleLine() {
        try{
            List<AdminRoleLineResponseDto> response = adminRoleLineService.getRoleLine();
            GenericResponse<List<AdminRoleLineResponseDto>> result = GenericResponse.<List<AdminRoleLineResponseDto>>builder()
                    .data(response)
                    .success(true)
                    .message("Fetched admin role line successfully")
                    .build();

            return ResponseEntity.ok(result);
        }catch (Exception e){
            GenericResponse<List<AdminRoleLineResponseDto>> error = GenericResponse.<List<AdminRoleLineResponseDto>>builder()
                    .data(null)
                    .success(false)
                    .message("Unable to fetch admin role line " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("update-admin-line")
    public ResponseEntity<GenericResponse<AdminRoleLineResponseDto>> updateAdminRoleLine(@RequestBody AdminRoleLineRequestDto dto) {

        try{
            AdminRoleLineResponseDto response = adminRoleLineService.updateRoleLine(dto);
            GenericResponse<AdminRoleLineResponseDto> result = GenericResponse.<AdminRoleLineResponseDto>builder()
                    .data(response)
                    .success(true)
                    .message("Admin role line updated successfully")
                    .build();

            return ResponseEntity.ok(result);
        }catch (Exception e){
            GenericResponse<AdminRoleLineResponseDto> error = GenericResponse.<AdminRoleLineResponseDto>builder()
                    .data(null)
                    .success(false)
                    .message("Unable to update admin role line " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(error);
        }
    }
}
