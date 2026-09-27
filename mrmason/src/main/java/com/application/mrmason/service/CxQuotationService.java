package com.application.mrmason.service;

import com.application.mrmason.dto.CxQuotationRequestDto;
import com.application.mrmason.dto.CxQuotationResponseDto;

import java.util.List;

public interface CxQuotationService {

    List<CxQuotationResponseDto> createQuotation(List<CxQuotationRequestDto> dtoList);

    CxQuotationResponseDto updateQuotation(CxQuotationRequestDto dto);

    List<CxQuotationResponseDto> getAllQuotation(String productCategory ,
                                                  String productSubCategory ,
                                                  String brand,
                                                  String stockKeepingUnit,
                                                  String productName,
                                                  String quantity,
                                                  String pincode,
                                                  String updatedBy,
                                                  String expectedDeliveryDate,
                                                  String updatedDate);

    List<CxQuotationResponseDto> getMyQuotation(CxQuotationRequestDto dto);
}
