package com.application.mrmason.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.PlumbingMasterRequest;
import com.application.mrmason.dto.PlumbingMasterResponse;
import com.application.mrmason.service.PlumbingMasterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/plumbing-master")
@RequiredArgsConstructor
public class PlumbingMasterController {

    private final PlumbingMasterService plumbingMasterService;

    // ============================================================
    // POST
    // MS ONLY
    // ============================================================

    @PreAuthorize("hasAuthority('MS')")
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody PlumbingMasterRequest request) {

        try {

            PlumbingMasterResponse response =
                    plumbingMasterService
                            .createPlumbingMaster(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // ============================================================
    // GET BY ID
    // MS ONLY
    // ============================================================

    @PreAuthorize("hasAuthority('MS')")
    @GetMapping("/{userIdStoreIdSku}")
    public ResponseEntity<?> getById(
            @PathVariable String userIdStoreIdSku) {

        try {

            PlumbingMasterResponse response =
                    plumbingMasterService
                            .getById(userIdStoreIdSku);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // ============================================================
    // GET BY STORE
    // MS ONLY
    // ============================================================

    @PreAuthorize("hasAuthority('MS')")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<?> getByStoreId(
            @PathVariable String storeId) {

        try {

            List<PlumbingMasterResponse> response =
                    plumbingMasterService
                            .getByStoreId(storeId);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // ============================================================
    // GET ALL
    // ALL USERS
    // ============================================================

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {

        try {

            List<PlumbingMasterResponse> response =
                    plumbingMasterService.getAll();

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('MS')")
     @GetMapping("/ms/all") 
     public ResponseEntity<?> getAllForMS() 
     { 
        try 
        {
             List<PlumbingMasterResponse> response = plumbingMasterService.getAll();
             return ResponseEntity.ok(response); 
            } catch (RuntimeException e) { 
                return ResponseEntity .status(HttpStatus.INTERNAL_SERVER_ERROR) .body(e.getMessage()); } 
            }
    // ============================================================
    // UPDATE
    // MS ONLY
    // ============================================================

    @PreAuthorize("hasAuthority('MS')")
    @PutMapping("/{userIdStoreIdSku}")
    public ResponseEntity<?> update(
            @PathVariable String userIdStoreIdSku,
            @RequestBody PlumbingMasterRequest request) {

        try {

            PlumbingMasterResponse response =
                    plumbingMasterService.update(
                            userIdStoreIdSku,
                            request);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}