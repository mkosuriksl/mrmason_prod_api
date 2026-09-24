package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
<<<<<<< HEAD
=======
import org.springframework.web.bind.annotation.PathVariable;
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.RequestParam;
=======
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
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
<<<<<<< HEAD
    // =========================================

    @PreAuthorize("hasAuthority('MS')")
    @GetMapping("/get-all-stores")
=======
    // All authenticated users can view
    // =========================================

  
    @GetMapping
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
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
<<<<<<< HEAD
    // ADD STORE
    // =========================================

    @PreAuthorize("hasAuthority('MS')")
=======
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
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
    @PostMapping
    public ResponseEntity<?> createStore(
            @RequestBody StoreMaster storeMaster) {

        try {

            StoreMaster store =
                    storeMasterService.createStore(storeMaster);

            return new ResponseEntity<>(
                    store,
<<<<<<< HEAD
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
=======
                    HttpStatus.CREATED);
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528

        } catch (Exception e) {

            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // =========================================
    // UPDATE STORE
<<<<<<< HEAD
    // =========================================

    @PreAuthorize("hasAuthority('MS')")
    @PutMapping
    public ResponseEntity<?> updateStore(
            @RequestParam("storeId") String storeId,
=======
    // Admin, Developer and Worker
    // =========================================

    @PreAuthorize("hasAuthority('MS')")
    @PutMapping("/{storeId}")
    public ResponseEntity<?> updateStore(
            @PathVariable String storeId,
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
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
<<<<<<< HEAD
    // =========================================

    @PreAuthorize("hasAuthority('MS')")
    @DeleteMapping
    public ResponseEntity<?> deleteStore(
            @RequestParam("storeId") String storeId) {
=======
    // Admin ONLY
    // =========================================

    @PreAuthorize("hasAuthority('MS')")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<?> deleteStore(
            @PathVariable String storeId) {
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528

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