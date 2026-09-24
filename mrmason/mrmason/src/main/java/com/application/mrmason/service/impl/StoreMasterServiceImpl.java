<<<<<<< HEAD

=======
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

<<<<<<< HEAD
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.StoreMaster;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.repository.StoreMasterRepository;
=======
import com.application.mrmason.entity.StoreMaster;
import com.application.mrmason.entity.User;
import com.application.mrmason.repository.StoreMasterRepository;
import com.application.mrmason.repository.UserDAO;
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.StoreMasterService;

@Service
public class StoreMasterServiceImpl implements StoreMasterService {

    @Autowired
    private StoreMasterRepository storeMasterRepository;

    @Autowired
<<<<<<< HEAD
    private MaterialSupplierQuotationUserDAO materialSupplierUserDAO;

    // ============================================================
    // CREATE STORE
    // ============================================================
=======
    private UserDAO userDAO;
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528

    @Override
    @Transactional
    public StoreMaster createStore(StoreMaster storeMaster) {

<<<<<<< HEAD
        // --------------------------------------------------------
        // STORE ID VALIDATION
        // --------------------------------------------------------

        if (storeMaster.getStoreId() == null
                || storeMaster.getStoreId().trim().isEmpty()) {
=======
        if (storeMaster.getStoreId() == null ||
                storeMaster.getStoreId().trim().isEmpty()) {
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528

            throw new IllegalArgumentException(
                    "Store ID is required");
        }

        if (storeMasterRepository.existsByStoreId(
                storeMaster.getStoreId())) {

            throw new IllegalArgumentException(
                    "Store ID already exists: "
                            + storeMaster.getStoreId());
        }

<<<<<<< HEAD
        // --------------------------------------------------------
        // STORE NAME VALIDATION
        // --------------------------------------------------------

        if (storeMaster.getStoreName() == null
                || storeMaster.getStoreName().trim().isEmpty()) {
=======
        if (storeMaster.getStoreName() == null ||
                storeMaster.getStoreName().trim().isEmpty()) {
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528

            throw new IllegalArgumentException(
                    "Store name is required");
        }

<<<<<<< HEAD
        // --------------------------------------------------------
        // GST VALIDATION
        // --------------------------------------------------------

        if (storeMaster.getGst() != null
                && !storeMaster.getGst().trim().isEmpty()) {
=======
        // Check GST only when GST is provided
        if (storeMaster.getGst() != null &&
                !storeMaster.getGst().trim().isEmpty()) {
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528

            if (storeMasterRepository.existsByGst(
                    storeMaster.getGst())) {

                throw new IllegalArgumentException(
                        "GST already exists: "
                                + storeMaster.getGst());
            }
        }

<<<<<<< HEAD
        // ========================================================
        // GET LOGGED-IN EMAIL
        // ========================================================

=======
        // Get logged-in user
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
        String email = AuthDetailsProvider.getLoggedEmail();

        if (email == null || email.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Logged-in user not found");
        }

<<<<<<< HEAD
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
=======
        User user = userDAO.findByEmail(email);

        if (user == null) {

            throw new IllegalArgumentException(
                    "User not found");
        }

        String userId = user.getBodSeqNo();

        // Code-derived fields
        storeMaster.setStoreIdUserId(
                storeMaster.getStoreId() + "_" + userId);
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528

        storeMaster.setUpdatedBy(userId);

        storeMaster.setUpdatedDate(
                LocalDateTime.now());

<<<<<<< HEAD
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

=======
        // Default status
        storeMaster.setVerifyStatus("INACTIVE");

        return storeMasterRepository.save(storeMaster);
    }

>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
    @Override
    public List<StoreMaster> getAllStores() {

        return storeMasterRepository.findAll();
    }

    @Override
<<<<<<< HEAD
public List<StoreMaster> getMyStores() {

=======
    public StoreMaster getStoreById(String storeId) {

        return storeMasterRepository
                .findByStoreId(storeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Store not found with storeId: "
                                        + storeId));
    }

    @Override
@Transactional
public StoreMaster updateStore(String storeId, StoreMaster storeDetails) {

    StoreMaster existingStore = storeMasterRepository
            .findByStoreId(storeId)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Store not found with storeId: " + storeId));

    // Update only non-null fields
    if (storeDetails.getStoreName() != null) {
        existingStore.setStoreName(storeDetails.getStoreName());
    }

    if (storeDetails.getGst() != null) {
        existingStore.setGst(storeDetails.getGst());
    }

    if (storeDetails.getAddress() != null) {
        existingStore.setAddress(storeDetails.getAddress());
    }

    if (storeDetails.getPincode() != null) {
        existingStore.setPincode(storeDetails.getPincode());
    }

    if (storeDetails.getState() != null) {
        existingStore.setState(storeDetails.getState());
    }

    if (storeDetails.getDistrict() != null) {
        existingStore.setDistrict(storeDetails.getDistrict());
    }

    if (storeDetails.getTown() != null) {
        existingStore.setTown(storeDetails.getTown());
    }

    if (storeDetails.getLandMark() != null) {
        existingStore.setLandMark(storeDetails.getLandMark());
    }

    // Get logged-in user
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
    String email = AuthDetailsProvider.getLoggedEmail();

    if (email == null || email.trim().isEmpty()) {
        throw new IllegalArgumentException(
                "Logged-in user not found");
    }

<<<<<<< HEAD
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

=======
    User user = userDAO.findByEmail(email);

    if (user == null) {
        throw new IllegalArgumentException(
                "User not found");
    }

    String userId = user.getBodSeqNo();

    // Audit fields
    existingStore.setStoreIdUserId(
            existingStore.getStoreId() + "_" + userId);

    existingStore.setUpdatedBy(userId);

    existingStore.setUpdatedDate(LocalDateTime.now());

    return storeMasterRepository.save(existingStore);
}

>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
    @Override
    @Transactional
    public void deleteStore(String storeId) {

<<<<<<< HEAD
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
=======
        StoreMaster store =
                storeMasterRepository
                        .findByStoreId(storeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Store not found with storeId: "
                                                + storeId));
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528

        storeMasterRepository.delete(store);
    }
}