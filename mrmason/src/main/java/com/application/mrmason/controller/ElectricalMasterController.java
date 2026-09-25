package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ElectricalMasterRequest;
import com.application.mrmason.dto.ElectricalMasterResponse;
import com.application.mrmason.service.ElectricalMasterService;

@RestController
@RequestMapping("/api/electrical-master")
public class ElectricalMasterController {

    @Autowired
    private ElectricalMasterService electricalMasterService;


    // ============================================================
    // CREATE ELECTRICAL MASTER
    // ============================================================
    //
    // POST
    // /api/electrical-master/create-electrical-master
    //
    // ONLY MATERIAL SUPPLIER
    //
    // ============================================================

    @PostMapping("create-electrical-master")
    @PreAuthorize("hasAuthority('MS')")
    public ResponseEntity<ElectricalMasterResponse> create(
            @RequestBody ElectricalMasterRequest request) {

        ElectricalMasterResponse response =
                electricalMasterService.create(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    // ============================================================
    // GET ELECTRICAL MASTER FOR MATERIAL SUPPLIER
    // ============================================================
    //
    // GET
    // /api/electrical-master/get-for-ms
    //
    // ONLY MATERIAL SUPPLIER
    //
    // ============================================================

    @GetMapping("get-for-ms")
    @PreAuthorize("hasAuthority('MS')")
    public ResponseEntity<List<ElectricalMasterResponse>> getForMs() {

        List<ElectricalMasterResponse> response =
                electricalMasterService.getForMs();

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // UPDATE ELECTRICAL MASTER
    // ============================================================
    //
    // PUT
    // /api/electrical-master
    //
    // ONLY MATERIAL SUPPLIER
    //
    // Example:
    //
    // /api/electrical-master?userIdSku=MS123_ELECTRICAL_WIRE_POLYCAB_80010001
    //
    // ============================================================

    @PutMapping
    @PreAuthorize("hasAuthority('MS')")
    public ResponseEntity<ElectricalMasterResponse> update(
            @RequestParam String userIdSku,
            @RequestBody ElectricalMasterRequest request) {

        ElectricalMasterResponse response =
                electricalMasterService.update(
                        userIdSku,
                        request);

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // GET ELECTRICAL MASTER FOR ALL USERS
    // ============================================================
    //
    // GET
    // /api/electrical-master/all
    //
    // AUTHENTICATED USERS
    //
    // ============================================================

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ElectricalMasterResponse>> getForAll() {

        List<ElectricalMasterResponse> response =
                electricalMasterService.getForAll();

        return ResponseEntity.ok(response);
    }
}