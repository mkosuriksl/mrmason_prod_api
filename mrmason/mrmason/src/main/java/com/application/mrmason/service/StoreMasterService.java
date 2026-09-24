package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.entity.StoreMaster;

public interface StoreMasterService {

    StoreMaster createStore(StoreMaster storeMaster);

    List<StoreMaster> getAllStores();

<<<<<<< HEAD
   
    StoreMaster updateStore(String storeId, StoreMaster storeMaster);

    void deleteStore(String storeId);

    List<StoreMaster> getMyStores();
=======
    StoreMaster getStoreById(String storeId);

    StoreMaster updateStore(String storeId, StoreMaster storeMaster);

    void deleteStore(String storeId);
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
}