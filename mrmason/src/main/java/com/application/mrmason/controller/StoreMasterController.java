package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    // All authenticated users can view
    // =========================================

  
    @GetMapping
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
    // GET STORE BY ID
    // All authenticated users can view
    // =========================================

    @GetMapping("/{storeId}")
    public ResponseEntity<?> getStoreById(
            @PathVariable String storeId) {

        try {

            StoreMaster store =
                    storeMasterService.getStoreById(storeId);

            return new ResponseEntity<>(
                    store,
                    HttpStatus.OK);

        } catch (Exception e) {

            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.NOT_FOUND);
        }
    }

    // =========================================
    // ADD STORE
    // Admin, Developer and Worker
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
                    HttpStatus.CREATED);

        } catch (Exception e) {

            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // =========================================
    // UPDATE STORE
    // Admin, Developer and Worker
    // =========================================

    @PreAuthorize("hasAuthority('MS')")
    @PutMapping("/{storeId}")
    public ResponseEntity<?> updateStore(
            @PathVariable String storeId,
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
    // Admin ONLY
    // =========================================

    @PreAuthorize("hasAuthority('MS')")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<?> deleteStore(
            @PathVariable String storeId) {

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