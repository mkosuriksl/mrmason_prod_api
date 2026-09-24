package com.application.mrmason.controller;

import java.util.List;

<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
=======
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.RequestParam;
=======
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.PlumbingMasterRequest;
import com.application.mrmason.dto.PlumbingMasterResponse;
import com.application.mrmason.service.PlumbingMasterService;

<<<<<<< HEAD
@RestController
@RequestMapping("/api/plumbing-master")
public class PlumbingMasterController {

    @Autowired
    private PlumbingMasterService plumbingMasterService;


    // ============================================================
    // CREATE PLUMBING MASTER
    // ============================================================
    //
    // POST
    // /api/plumbing-master
    //
    // ONLY MATERIAL SUPPLIER
    //
    // ============================================================

    @PostMapping("create-plumbing-master")
    @PreAuthorize("hasAuthority('MS')")
    public ResponseEntity<PlumbingMasterResponse> create(
            @RequestBody PlumbingMasterRequest request) {

        PlumbingMasterResponse response =
                plumbingMasterService.create(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    // ============================================================
    // GET PLUMBING MASTER FOR MATERIAL SUPPLIER
    // ============================================================
    //
    // GET
    // /api/plumbing-master
    //
    // ONLY MATERIAL SUPPLIER
    //
    // Example:
    //
    // /api/plumbing-master?storeId=1
    //
    // /api/plumbing-master?storeId=1&productCategory=Plumbing
    //
    // ============================================================

    @GetMapping("get-for-ms")
    @PreAuthorize("hasAuthority('MS')")
    public ResponseEntity<PlumbingMasterResponse> getForMs(

            @RequestParam(required = false)
            String storeId,

            @RequestParam(required = false)
            String updatedBy,

            @RequestParam(required = false)
            String productCategory,

            @RequestParam(required = false)
            String productSubCategory) {

        PlumbingMasterResponse response =
                plumbingMasterService.getForMs(
                        storeId,
                        updatedBy,
                        productCategory,
                        productSubCategory);

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // UPDATE PLUMBING MASTER
    // ============================================================
    //
    // PUT
    // /api/plumbing-master/{userIdStoreIdSku}
    //
    // ONLY MATERIAL SUPPLIER
    //
    // Example:
    //
    // /api/plumbing-master/MS123_1_70011505
    //
    // ============================================================
@PutMapping
@PreAuthorize("hasAuthority('MS')")
public ResponseEntity<PlumbingMasterResponse> update(
        @RequestParam String userIdStoreIdSku,
        @RequestBody PlumbingMasterRequest request) {

    PlumbingMasterResponse response =
            plumbingMasterService.update(
                    userIdStoreIdSku,
                    request);

    return ResponseEntity.ok(response);
}

    // ============================================================
    // GET PLUMBING MASTER FOR ALL USERS
    // ============================================================
    //
    // GET
    // /api/plumbing-master/all
    //
    // AUTHENTICATED USERS
    //
    // ============================================================

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlumbingMasterResponse>> getForAll() {

        List<PlumbingMasterResponse> response =
                plumbingMasterService.getForAll();

        return ResponseEntity.ok(response);
=======
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
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
    }
}