package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.entity.StoreMaster;

public interface StoreMasterService {

    StoreMaster createStore(StoreMaster storeMaster);

    List<StoreMaster> getAllStores();

    StoreMaster getStoreById(String storeId);

    StoreMaster updateStore(String storeId, StoreMaster storeMaster);

    void deleteStore(String storeId);
}