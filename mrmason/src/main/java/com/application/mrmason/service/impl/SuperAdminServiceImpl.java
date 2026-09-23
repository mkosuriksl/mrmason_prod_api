package com.application.mrmason.service.impl;

import com.application.mrmason.dto.SuperAdminLoginRequestDto;
import com.application.mrmason.dto.SuperAdminLoginResponseDto;
import com.application.mrmason.dto.SuperAdminRequestDto;
import com.application.mrmason.dto.SuperAdminResponseDto;
import com.application.mrmason.entity.SuperAdmin;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.repository.SuperAdminRepository;
import com.application.mrmason.security.JwtService;
import com.application.mrmason.service.SuperAdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuperAdminServiceImpl implements SuperAdminService {

    private final SuperAdminRepository superAdminRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public SuperAdminResponseDto createSuperAdmin(SuperAdminRequestDto dto) {

        if (dto.getEmail() == null || dto.getMobileNumber() == null) {
            throw new IllegalArgumentException("Email and Mobile Number must not be null");
        }

        if (superAdminRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email address already registered. Please login.");
        }

        boolean mobileNumberExist = superAdminRepository.existsByMobileNumber(dto.getMobileNumber());
        if (mobileNumberExist) {
            throw new RuntimeException("Mobile Number already exists. Please login");
        }

        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uniqueId = "SADM" + dateTime;

        SuperAdmin adminEntity = SuperAdmin.builder()
                .firstName(dto.getFirstName())
                .superAdminId(uniqueId)
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .mobileNumber(dto.getMobileNumber())
                .updatedBy(uniqueId)
                .updatedDate(LocalDateTime.now())
                .userType(UserType.SADM)
                .isVerified(true)
                .otp(null)
                .build();

        SuperAdmin savedSuperAdmin = superAdminRepository.save(adminEntity);
        log.info("SuperAdmin successfully created with ID: {}", savedSuperAdmin.getSuperAdminId());

        return mapToResponseDto(savedSuperAdmin);

    }
    @Transactional
    public SuperAdminLoginResponseDto loginSuperAdmin(SuperAdminLoginRequestDto dto) {

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Username cannot be empty");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password cannot be empty");
        }

        String username = dto.getEmail().trim();
        log.info("Login attempt for username :{}", username);

        SuperAdmin superAdmin = superAdminRepository.findBySuperAdminEmail(username)

                .orElseThrow(() -> {
                    log.warn("Super Admin not found with username: {}", username);
                    return new RuntimeException("Invalid username or password");
                });


        if (!superAdmin.getIsVerified()) {
            log.warn("Unverified account attempted login: {}", username);
            throw new RuntimeException("Please verify your account first. Check your email for verification link..");
        }

        boolean matches = passwordEncoder.matches(dto.getPassword(), superAdmin.getPassword());

        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (AuthenticationException e) {
            log.error("Authentication failed class: {} | message: {}", e.getClass().getName(), e.getMessage());
            throw new RuntimeException("Invalid username or password");
        }


        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(superAdmin.getEmail())
                .password(superAdmin.getPassword())
                .roles(superAdmin.getUserType().name())
                .build();
        String token = jwtService.generateToken(userDetails, superAdmin.getSuperAdminId());

        superAdminRepository.save(superAdmin);

        SuperAdminLoginResponseDto response = SuperAdminLoginResponseDto.builder()
                .superAdminId(superAdmin.getSuperAdminId())
                .email(superAdmin.getEmail())
                .mobileNumber(superAdmin.getMobileNumber())
                .firstName(superAdmin.getFirstName())
                .lastName(superAdmin.getLastName())
                .isVerified(true)
                .token(token)
                .userType(UserType.SADM)
                .updatedBy(superAdmin.getSuperAdminId())
                .updatedDate(LocalDateTime.now())
                .build();
        return response;
    }

    @Override
    public SuperAdminResponseDto getSuperAdmin(SuperAdminRequestDto superAdminRequestDto) {
        return null;
    }


    private SuperAdminResponseDto mapToResponseDto(SuperAdmin entity) {
        return SuperAdminResponseDto.builder()
                .superAdminId(entity.getSuperAdminId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .mobileNumber(entity.getMobileNumber())
                .userType(entity.getUserType())
                .isVerified(entity.getIsVerified())
                .updatedBy(entity.getUpdatedBy())
                .updatedDate(entity.getUpdatedDate())
                .build();
    }
}
