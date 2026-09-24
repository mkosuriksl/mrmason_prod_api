
package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.StoreMaster;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.repository.StoreMasterRepository;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.StoreMasterService;

@Service
public class StoreMasterServiceImpl implements StoreMasterService {

    @Autowired
    private StoreMasterRepository storeMasterRepository;

    @Autowired
    private MaterialSupplierQuotationUserDAO materialSupplierUserDAO;

    // ============================================================
    // CREATE STORE
    // ============================================================

    @Override
    @Transactional
    public StoreMaster createStore(StoreMaster storeMaster) {

        // --------------------------------------------------------
        // STORE ID VALIDATION
        // --------------------------------------------------------

        if (storeMaster.getStoreId() == null
                || storeMaster.getStoreId().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Store ID is required");
        }

        if (storeMasterRepository.existsByStoreId(
                storeMaster.getStoreId())) {

            throw new IllegalArgumentException(
                    "Store ID already exists: "
                            + storeMaster.getStoreId());
        }

        // --------------------------------------------------------
        // STORE NAME VALIDATION
        // --------------------------------------------------------

        if (storeMaster.getStoreName() == null
                || storeMaster.getStoreName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Store name is required");
        }

        // --------------------------------------------------------
        // GST VALIDATION
        // --------------------------------------------------------

        if (storeMaster.getGst() != null
                && !storeMaster.getGst().trim().isEmpty()) {

            if (storeMasterRepository.existsByGst(
                    storeMaster.getGst())) {

                throw new IllegalArgumentException(
                        "GST already exists: "
                                + storeMaster.getGst());
            }
        }

        // ========================================================
        // GET LOGGED-IN EMAIL
        // ========================================================

        String email = AuthDetailsProvider.getLoggedEmail();

        if (email == null || email.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Logged-in user not found");
        }

       String loggedInEmail = email.trim();

        // ========================================================
        // CHECK MATERIAL SUPPLIER REGISTRATION
        // ========================================================

        MaterialSupplierQuotationUser supplier =
                materialSupplierUserDAO
                        .findByEmailAndRegSource(
                                loggedInEmail,
                                RegSource.MRMASON)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Material Supplier registration "
                                                + "not found for email: "
                                                + email
                                                + ", regSource: MRMASON"));

        // ========================================================
        // CHECK SUPPLIER STATUS
        // ========================================================

        if (supplier.getStatus() == null
                || !"active".equalsIgnoreCase(
                        supplier.getStatus())) {

            throw new IllegalArgumentException(
                    "Material Supplier is not active for email: "
                            + email);
        }

        // ========================================================
        // GET SUPPLIER BOD SEQ NO
        // ========================================================

        String userId = supplier.getBodSeqNo();

        if (userId == null || userId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "bodSeqNo not found for Material Supplier: "
                            + email);
        }

        userId = userId.trim();

        // ========================================================
        // SET CODE-DERIVED FIELDS
        // ========================================================

        storeMaster.setStoreIdUserId(
                storeMaster.getStoreId()
                        + "_"
                        + userId);

        storeMaster.setUpdatedBy(userId);

        storeMaster.setUpdatedDate(
                LocalDateTime.now());

        // ========================================================
        // DEFAULT VERIFY STATUS
        // ========================================================

        storeMaster.setVerifyStatus("INACTIVE");

        // ========================================================
        // SAVE STORE
        // ========================================================

        return storeMasterRepository.save(storeMaster);
    }

    // ============================================================
    // GET ALL STORES
    // ============================================================

    @Override
    public List<StoreMaster> getAllStores() {

        return storeMasterRepository.findAll();
    }

    @Override
public List<StoreMaster> getMyStores() {

    String email = AuthDetailsProvider.getLoggedEmail();

    if (email == null || email.trim().isEmpty()) {
        throw new IllegalArgumentException(
                "Logged-in user not found");
    }

    String loggedInEmail = email.trim();

    MaterialSupplierQuotationUser supplier =
            materialSupplierUserDAO
                    .findByEmailAndRegSource(
                            loggedInEmail,
                            RegSource.MRMASON)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Material Supplier registration not found for email: "
                                            + loggedInEmail
                                            + ", regSource: MRMASON"));

    if (supplier.getStatus() == null
            || !"active".equalsIgnoreCase(
                    supplier.getStatus())) {

        throw new IllegalArgumentException(
                "Material Supplier is not active for email: "
                        + loggedInEmail);
    }

    String userId = supplier.getBodSeqNo();

    if (userId == null || userId.trim().isEmpty()) {
        throw new IllegalArgumentException(
                "bodSeqNo not found for Material Supplier: "
                        + loggedInEmail);
    }

    userId = userId.trim();

    return storeMasterRepository.findByUpdatedBy(userId);
}

   
  

    // ============================================================
    // UPDATE STORE
    // ============================================================

    @Override
    @Transactional
    public StoreMaster updateStore(
            String storeId,
            StoreMaster storeDetails) {

        // ========================================================
        // STORE ID VALIDATION
        // ========================================================

        if (storeId == null || storeId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Store ID is required");
        }

       String trimmedStoreId = storeId.trim();

        // ========================================================
        // GET LOGGED-IN EMAIL FROM JWT
        // ========================================================

        String email = AuthDetailsProvider.getLoggedEmail();

        if (email == null || email.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Logged-in user not found");
        }

        String loggedInEmail = email.trim();

        // ========================================================
        // GET MATERIAL SUPPLIER
        // ========================================================

        MaterialSupplierQuotationUser supplier =
                materialSupplierUserDAO
                        .findByEmailAndRegSource(
                                loggedInEmail,
                                RegSource.MRMASON)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Material Supplier registration "
                                                + "not found for email: "
                                                + loggedInEmail
                                                + ", regSource: MRMASON"));

        // ========================================================
        // CHECK SUPPLIER STATUS
        // ========================================================

        if (supplier.getStatus() == null
                || !"active".equalsIgnoreCase(
                        supplier.getStatus())) {

            throw new IllegalArgumentException(
                    "Material Supplier is not active for email: "
                            + loggedInEmail);
        }

        // ========================================================
        // GET SUPPLIER BOD SEQ NO FROM JWT USER
        // ========================================================

        String userId = supplier.getBodSeqNo();

        if (userId == null || userId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "bodSeqNo not found for Material Supplier: "
                            + loggedInEmail);
        }

        userId = userId.trim();

        // ========================================================
        // FIND ONLY THIS SUPPLIER'S STORE
        // ========================================================

        StoreMaster existingStore =
                storeMasterRepository
                        .findByStoreIdAndUpdatedBy(
                                storeId,
                                userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Store not found or does not "
                                                + "belong to the logged-in "
                                                + "Material Supplier: "
                                                + trimmedStoreId));

        // ========================================================
        // UPDATE STORE NAME
        // ========================================================

        if (storeDetails.getStoreName() != null) {

            existingStore.setStoreName(
                    storeDetails.getStoreName());
        }

        // ========================================================
        // UPDATE GST
        // ========================================================

        if (storeDetails.getGst() != null
                && !storeDetails.getGst().trim().isEmpty()
                && !storeDetails.getGst().trim().equalsIgnoreCase(
                        existingStore.getGst())) {

            String gst = storeDetails.getGst().trim();

            if (storeMasterRepository.existsByGst(gst)) {

                throw new IllegalArgumentException(
                        "GST already exists: " + gst);
            }

            existingStore.setGst(gst);
        }

        // ========================================================
        // UPDATE ADDRESS
        // ========================================================

        if (storeDetails.getAddress() != null) {

            existingStore.setAddress(
                    storeDetails.getAddress());
        }

        // ========================================================
        // UPDATE PINCODE
        // ========================================================

        if (storeDetails.getPincode() != null) {

            existingStore.setPincode(
                    storeDetails.getPincode());
        }

        // ========================================================
        // UPDATE STATE
        // ========================================================

        if (storeDetails.getState() != null) {

            existingStore.setState(
                    storeDetails.getState());
        }

        // ========================================================
        // UPDATE DISTRICT
        // ========================================================

        if (storeDetails.getDistrict() != null) {

            existingStore.setDistrict(
                    storeDetails.getDistrict());
        }

        // ========================================================
        // UPDATE TOWN
        // ========================================================

        if (storeDetails.getTown() != null) {

            existingStore.setTown(
                    storeDetails.getTown());
        }

        // ========================================================
        // UPDATE LANDMARK
        // ========================================================

        if (storeDetails.getLandMark() != null) {

            existingStore.setLandMark(
                    storeDetails.getLandMark());
        }

        // ========================================================
        // UPDATE AUDIT FIELDS
        // ========================================================

        existingStore.setStoreIdUserId(
                existingStore.getStoreId()
                        + "_" + userId);

        existingStore.setUpdatedBy(userId);

        existingStore.setUpdatedDate(
                LocalDateTime.now());

        // ========================================================
        // SAVE
        // ========================================================

        return storeMasterRepository.save(existingStore);
    }

    // ============================================================
    // DELETE STORE
    // ============================================================

    @Override
    @Transactional
    public void deleteStore(String storeId) {

        // ========================================================
        // STORE ID VALIDATION
        // ========================================================

        if (storeId == null || storeId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Store ID is required");
        }

        String trimmedStoreId = storeId.trim();

        // ========================================================
        // GET LOGGED-IN EMAIL FROM JWT
        // ========================================================

        String email = AuthDetailsProvider.getLoggedEmail();

        if (email == null || email.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Logged-in user not found");
        }

        String loggedInEmail = email.trim();

        // ========================================================
        // GET MATERIAL SUPPLIER
        // ========================================================

        MaterialSupplierQuotationUser supplier =
                materialSupplierUserDAO
                        .findByEmailAndRegSource(
                                loggedInEmail,
                                RegSource.MRMASON)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Material Supplier registration "
                                                + "not found for email: "
                                                + loggedInEmail
                                                + ", regSource: MRMASON"));

        // ========================================================
        // CHECK SUPPLIER STATUS
        // ========================================================

        if (supplier.getStatus() == null
                || !"active".equalsIgnoreCase(
                        supplier.getStatus())) {

            throw new IllegalArgumentException(
                    "Material Supplier is not active for email: "
                            + loggedInEmail);
        }

        // ========================================================
        // GET SUPPLIER BOD SEQ NO FROM JWT USER
        // ========================================================

        String userId = supplier.getBodSeqNo();

        if (userId == null || userId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "bodSeqNo not found for Material Supplier: "
                            + loggedInEmail);
        }

        userId = userId.trim();

        // ========================================================
        // FIND ONLY THIS SUPPLIER'S STORE
        // ========================================================

        StoreMaster store =
                storeMasterRepository
                        .findByStoreIdAndUpdatedBy(
                                storeId,
                                userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Store not found or does not "
                                                + "belong to the logged-in "
                                                + "Material Supplier: "
                                                + trimmedStoreId));

        // ========================================================
        // DELETE
        // ========================================================

        storeMasterRepository.delete(store);
    }
}