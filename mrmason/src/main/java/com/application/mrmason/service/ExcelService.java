package com.application.mrmason.service;

import com.application.mrmason.entity.PlumbingMaster;

public interface ExcelService {

    void importPlumbingMasterFromExcel();

    void addPlumbingMasterToExcel(
            PlumbingMaster plumbingMaster);

    void updatePlumbingMasterInExcel(
            PlumbingMaster plumbingMaster);

    void deletePlumbingMasterFromExcel(
            String userIdStoreIdSku);
}