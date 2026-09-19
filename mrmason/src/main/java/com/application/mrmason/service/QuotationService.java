package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.entity.QuotationEntity;
import com.application.mrmason.enums.RegSource;

public interface QuotationService {
	List<QuotationEntity> createQuotation(List<QuotationEntity> quotation);
    public List<QuotationEntity> getQuotation(String reqId,String servicePersonId, String customerId, String updatedBy);
    QuotationEntity updateQuotation(QuotationEntity updatedQuotation,RegSource regSource);
}
