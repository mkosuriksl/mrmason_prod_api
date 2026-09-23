package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.entity.StoreMaster;
import com.application.mrmason.service.StoreMasterService;

@RestController
@RequestMapping("/api/store-master")
public class StoreMasterController {

    @Autowired
    private StoreMasterService storeMasterService;

    // =========================================
    // GET ALL STORES
    // =========================================

    @PreAuthorize("hasAuthority('MS')")
    @GetMapping("/get-all-stores")
    public ResponseEntity<?> getAllStores() {

        try {

            List<StoreMaster> stores =
                    storeMasterService.getAllStores();

            return new ResponseEntity<>(
                    stores,
                    HttpStatus.OK);

        } catch (Exception e) {

            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =========================================
    // ADD STORE
    // =========================================

    @PreAuthorize("hasAuthority('MS')")
    @PostMapping
    public ResponseEntity<?> createStore(
            @RequestBody StoreMaster storeMaster) {

        try {

            StoreMaster store =
                    storeMasterService.createStore(storeMaster);

            return new ResponseEntity<>(
                    store,
                    HttpStatus.OK);

        } catch (Exception e) {

            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // =========================================
    // GET MY STORES
    // =========================================

    @PreAuthorize("hasAuthority('MS')")
    @GetMapping("/my-stores")
    public ResponseEntity<?> getMyStores() {

        try {

            List<StoreMaster> stores =
                    storeMasterService.getMyStores();

            return ResponseEntity.ok(stores);

        } catch (Exception e) {

            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // =========================================
    // UPDATE STORE
    // =========================================

    @PreAuthorize("hasAuthority('MS')")
    @PutMapping
    public ResponseEntity<?> updateStore(
            @RequestParam("storeId") String storeId,
            @RequestBody StoreMaster storeMaster) {

        try {

            StoreMaster store =
                    storeMasterService.updateStore(
                            storeId,
                            storeMaster);

            return new ResponseEntity<>(
                    store,
                    HttpStatus.OK);

        } catch (Exception e) {

            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // =========================================
    // DELETE STORE
    // =========================================

    @PreAuthorize("hasAuthority('MS')")
    @DeleteMapping
    public ResponseEntity<?> deleteStore(
            @RequestParam("storeId") String storeId) {

        try {

            storeMasterService.deleteStore(storeId);

            return new ResponseEntity<>(
                    "Store deleted successfully",
                    HttpStatus.OK);

        } catch (Exception e) {

            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.NOT_FOUND);
        }
    }
}