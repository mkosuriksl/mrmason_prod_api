package com.application.mrmason.service;

import com.application.mrmason.dto.CxQuotationRequestDto;
import com.application.mrmason.dto.CxQuotationResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CxQuotationService {

    public List<CxQuotationResponseDto> createQuotation(List<CxQuotationRequestDto> dto);

    public CxQuotationResponseDto updateQuotation(CxQuotationRequestDto dto);

    public List<CxQuotationResponseDto> getAllQuotation(String productCategory ,
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
