
package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.ElectricalMasterRequest;
import com.application.mrmason.dto.ElectricalMasterResponse;

public interface ElectricalMasterService {

    // ============================================================
    // CREATE
    // ============================================================

    ElectricalMasterResponse create(
            ElectricalMasterRequest request);


    // ============================================================
    // GET FOR MATERIAL SUPPLIER
    // ============================================================

    List<ElectricalMasterResponse> getForMs(
            );


    // ============================================================
    // UPDATE
    // ============================================================

    ElectricalMasterResponse update(
            String userIdStoreIdSku,
            ElectricalMasterRequest request);


    // ============================================================
    // GET FOR ALL USERS
    // ============================================================

    List<ElectricalMasterResponse> getForAll();
<<<<<<< Updated upstream:mrmason/src/main/java/com/application/mrmason/service/ElectricalMasterService.java
}
=======
} 
>>>>>>> Stashed changes:mrmason/mrmason/src/main/java/com/application/mrmason/service/ElectricalMasterService.java
