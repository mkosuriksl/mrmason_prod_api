package com.application.mrmason.repository;

<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.StoreMaster;

@Repository
public interface StoreMasterRepository
        extends JpaRepository<StoreMaster, String> {

    boolean existsByStoreId(String storeId);

    boolean existsByGst(String gst);
<<<<<<< HEAD
    Optional<StoreMaster> findByStoreIdAndUpdatedBy(
        String storeId,
        String userId);

    Optional<StoreMaster> findByStoreId(String storeId);

    List<StoreMaster> findByUpdatedBy(String userId);
=======

    Optional<StoreMaster> findByStoreId(String storeId);
>>>>>>> 9eb01aa08e6909cbd76547d5f9adfd2374a3a528
}