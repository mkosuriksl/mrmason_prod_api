package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.mrmason.entity.StoreMaster;
import com.application.mrmason.entity.User;
import com.application.mrmason.repository.StoreMasterRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.StoreMasterService;

@Service
public class StoreMasterServiceImpl implements StoreMasterService {

    @Autowired
    private StoreMasterRepository storeMasterRepository;

    @Autowired
    private UserDAO userDAO;

    @Override
    @Transactional
    public StoreMaster createStore(StoreMaster storeMaster) {

        if (storeMaster.getStoreId() == null ||
                storeMaster.getStoreId().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Store ID is required");
        }

        if (storeMasterRepository.existsByStoreId(
                storeMaster.getStoreId())) {

            throw new IllegalArgumentException(
                    "Store ID already exists: "
                            + storeMaster.getStoreId());
        }

        if (storeMaster.getStoreName() == null ||
                storeMaster.getStoreName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Store name is required");
        }

        // Check GST only when GST is provided
        if (storeMaster.getGst() != null &&
                !storeMaster.getGst().trim().isEmpty()) {

            if (storeMasterRepository.existsByGst(
                    storeMaster.getGst())) {

                throw new IllegalArgumentException(
                        "GST already exists: "
                                + storeMaster.getGst());
            }
        }

        // Get logged-in user
        String email = AuthDetailsProvider.getLoggedEmail();

        if (email == null || email.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Logged-in user not found");
        }

        User user = userDAO.findByEmail(email);

        if (user == null) {

            throw new IllegalArgumentException(
                    "User not found");
        }

        String userId = user.getBodSeqNo();

        // Code-derived fields
        storeMaster.setStoreIdUserId(
                storeMaster.getStoreId() + "_" + userId);

        storeMaster.setUpdatedBy(userId);

        storeMaster.setUpdatedDate(
                LocalDateTime.now());

        // Default status
        storeMaster.setVerifyStatus("INACTIVE");

        return storeMasterRepository.save(storeMaster);
    }

    @Override
    public List<StoreMaster> getAllStores() {

        return storeMasterRepository.findAll();
    }

    @Override
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
    String email = AuthDetailsProvider.getLoggedEmail();

    if (email == null || email.trim().isEmpty()) {
        throw new IllegalArgumentException(
                "Logged-in user not found");
    }

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

    @Override
    @Transactional
    public void deleteStore(String storeId) {

        StoreMaster store =
                storeMasterRepository
                        .findByStoreId(storeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Store not found with storeId: "
                                                + storeId));

        storeMasterRepository.delete(store);
    }
}