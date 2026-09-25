package com.application.mrmason.service.impl;

import com.application.mrmason.dto.CxQuotationRequestDto;
import com.application.mrmason.dto.CxQuotationResponseDto;
import com.application.mrmason.entity.*;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.*;
import com.application.mrmason.service.CxQuotationService;
import com.application.mrmason.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CxQuotationServiceImpl implements CxQuotationService {

    private final CxQuotationRepository cxQuotationRepository;
    private final CxMaterialRequestHeaderRepository cxMaterialRequestHeaderRepository;
    private final CxQuotationHeaderDetailRepository cxQuotationHeaderDetailRepository;
    private final CustomerRegistrationRepo customerRegistrationRepo;
    private final AdminDetailsRepo adminDetailsRepo;
    private final EmailService emailService;

    @Override
    @Transactional
    public List<CxQuotationResponseDto> createQuotation(List<CxQuotationRequestDto> dtoList) {

        if (dtoList == null || dtoList.isEmpty()) {
            throw new IllegalArgumentException("Quotation request list cannot be null or empty");
        }

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        CustomerRegistration customer = customerRegistrationRepo.findByUserEmailOne(currentEmail)
                .orElseThrow(() -> new RuntimeException("No customer found for email: " + currentEmail));

        String loggedUser = customer.getUserid();
        LocalDateTime now = LocalDateTime.now();

        String dateTimestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String basePrefix = "CMR" + dateTimestamp;

        List<CxQuotationResponseDto> responseList = new ArrayList<>();

        List<CxMaterialQuotationRequestHeader> headersToSave = new ArrayList<>();
        List<CxMaterialQuotationRequestHeaderDetails> detailsToSave = new ArrayList<>();

        for (CxQuotationRequestDto requestDto : dtoList) {

            List<CxQuotationResponseDto.CxQuotationHeader> responseHeaders = new ArrayList<>();

            String matRequestDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            CxQuotationResponseDto.CxQuotationHeader responseHeader = CxQuotationResponseDto.CxQuotationHeader.builder()
                    .materialRequestId(basePrefix)
                    .requestDate(matRequestDate)
                    .requestStatus("New")
                    .materialRequestRequestedBy(loggedUser)
                    .build();

            responseHeaders.add(responseHeader);
            CxMaterialQuotationRequestHeader headerEntity = CxMaterialQuotationRequestHeader.builder()
                    .materialRequestId(basePrefix)
                    .requestDate(responseHeader.getRequestDate())
                    .requestStatus(responseHeader.getRequestStatus())
                    .materialRequestRequestedBy(loggedUser)
                    .updatedBy(loggedUser)
                    .updatedDate(now)
                    .expectedDeliveryDate(requestDto.getExpectedDeliveryDate())
                    .deliveryLocation(requestDto.getDeliveryLocation())
                    .pincode(requestDto.getPincode())
                    .build();

            headersToSave.add(headerEntity);

            List<CxQuotationResponseDto.CxQuotationHeaderDetail> responseDetails = new ArrayList<>();
            if (requestDto.getHeaderDetail() != null) {
                int lineCounter = 1;
                for (CxQuotationRequestDto.CxQuotationHeaderDetail detailItem : requestDto.getHeaderDetail()) {
                    String generatedLineId = String.format("%s%02d", basePrefix, lineCounter++);

                    String quoteId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                    CxQuotationResponseDto.CxQuotationHeaderDetail responseDetail = CxQuotationResponseDto.CxQuotationHeaderDetail.builder()
                            .quotationRequestLineId(generatedLineId)
                            .quotationId(quoteId)
                            .productCategory(detailItem.getProductCategory())
                            .productSubCategory(detailItem.getProductSubCategory())
                            .brand(detailItem.getBrand())
                            .sku(detailItem.getSku())
                            .productName(detailItem.getProductName())
                            .quantity(detailItem.getQuantity())
                            .build();

                    responseDetails.add(responseDetail);
                    CxMaterialQuotationRequestHeaderDetails detailEntity = CxMaterialQuotationRequestHeaderDetails.builder()
                            .quotationRequestLineId(generatedLineId)
                            .quotationId(quoteId)
                            .productCategory(detailItem.getProductCategory())
                            .productSubCategory(detailItem.getProductSubCategory())
                            .brand(detailItem.getBrand())
                            .sku(detailItem.getSku())
                            .productName(detailItem.getProductName())
                            .quantity(detailItem.getQuantity())
                            .updatedBy(loggedUser)
                            .updatedDate(now)
                            .build();

                    detailsToSave.add(detailEntity);
                }
            }
            CxQuotationResponseDto responseDto = CxQuotationResponseDto.builder()
                    .updatedBy(loggedUser)
                    .updatedDate(now)
                    .expectedDeliveryDate(requestDto.getExpectedDeliveryDate())
                    .deliveryLocation(requestDto.getDeliveryLocation())
                    .pincode(requestDto.getPincode())
                    .headerlist(responseHeaders)
                    .headerDetail(responseDetails)
                    .build();

            responseList.add(responseDto);

            if (requestDto.getHeaderDetail() != null && !requestDto.getHeaderDetail().isEmpty()) {
                for (CxQuotationRequestDto.CxQuotationHeaderDetail detailItem : requestDto.getHeaderDetail()) {
                    String subject = "Confirmed Quotation - " + detailItem.getProductName();
                    String body = String.format(
                            "Dear %s,<br><br>" +
                                    "Your quotation for '%s' (Quantity: %s) has been successfully created under Request ID: %s." +
                                    "<br><br>Thank You!",
                            customer.getCustomerName(),
                            detailItem.getProductName(),
                            detailItem.getQuantity(),
                            basePrefix);
                    RegSource regSource = customer.getRegSource();

                    emailService.sendEmail(customer.getUserEmail(), subject, body);
                }
            }
        }

        if (!headersToSave.isEmpty()) {
            cxQuotationRepository.saveAll(headersToSave);
        }
        if (!detailsToSave.isEmpty()) {
            cxQuotationHeaderDetailRepository.saveAll(detailsToSave);
        }

        return responseList;
    }

    @Override
    public List<CxQuotationResponseDto> getAllQuotation(String productCategory, String productSubCategory, String brand, String stockKeepingUnit, String productName, String quantity, String pincode, String updatedBy, String expectedDeliveryDate, String updatedDate) {

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        AdminDetails admin = adminDetailsRepo.findByEmail(currentEmail);
        if (admin == null) {
            throw new SecurityException("Access denied: Only Admin and Material Supplier roles can access all quotations.");
        }

        UserType userRole = admin.getUserType();
        if (userRole == null || (!userRole.equals(UserType.Adm) && !userRole.equals(UserType.MS))) {
            throw new SecurityException("Access denied: Only ADMIN and MS roles can access all quotations.");
        }

        List<CxMaterialQuotationRequestHeaderDetails> detailsList = cxQuotationHeaderDetailRepository.searchAllQuotationDetails(
                productCategory, productSubCategory, brand, stockKeepingUnit,
                productName, quantity, pincode, updatedBy, expectedDeliveryDate, updatedDate
        );

        Map<String, List<CxMaterialQuotationRequestHeaderDetails>> groupedDetails = detailsList.stream()
                .collect(Collectors.groupingBy(detail -> detail.getQuotationRequestLineId().split("_")[0]));

        List<CxQuotationResponseDto> responseList = new ArrayList<>();

        for (Map.Entry<String, List<CxMaterialQuotationRequestHeaderDetails>> entry : groupedDetails.entrySet()) {
            String materialRequestId = entry.getKey();
            List<CxMaterialQuotationRequestHeaderDetails> lineItems = entry.getValue();

            CxMaterialQuotationRequestHeader header = cxQuotationRepository.findById(materialRequestId)
                    .orElse(null);

            if (header != null) {
                CxQuotationResponseDto.CxQuotationHeader responseHeader = CxQuotationResponseDto.CxQuotationHeader.builder()
                        .materialRequestId(header.getMaterialRequestId())
                        .requestDate(header.getRequestDate())
                        .requestStatus(header.getRequestStatus())
                        .materialRequestRequestedBy(header.getMaterialRequestRequestedBy())
                        .build();

                List<CxQuotationResponseDto.CxQuotationHeaderDetail> responseDetails = lineItems.stream()
                        .map(item -> CxQuotationResponseDto.CxQuotationHeaderDetail.builder()
                                .quotationRequestLineId(item.getQuotationRequestLineId())
                                .quotationId(item.getQuotationId())
                                .productCategory(item.getProductCategory())
                                .productSubCategory(item.getProductSubCategory())
                                .brand(item.getBrand())
                                .sku(item.getSku())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList());

                // Fetch customer details
                CxQuotationResponseDto.CustomerDetails customerDetails = null;
                if (header.getUpdatedBy() != null && !header.getUpdatedBy().isEmpty()) {
                    CustomerRegistration customer = customerRegistrationRepo.findByUserid(header.getUpdatedBy());
                    if (customer != null) {
                        customerDetails = CxQuotationResponseDto.CustomerDetails.builder()
                                .name(customer.getCustomerName())
                                .email(customer.getUserEmail())
                                .mobile(customer.getUserMobile())
                                .userId(customer.getUserid())
                                .userType(customer.getUserType())
                                .regSource(customer.getRegSource())
                                .build();
                    }
                }

                CxQuotationResponseDto responseDto = CxQuotationResponseDto.builder()
                        .updatedBy(header.getUpdatedBy())
                        .updatedDate(header.getUpdatedDate())
                        .expectedDeliveryDate(header.getExpectedDeliveryDate())
                        .deliveryLocation(header.getDeliveryLocation())
                        .pincode(header.getPincode())
                        .headerlist(List.of(responseHeader))
                        .headerDetail(responseDetails)
                        .customerDetails(customerDetails)
                        .build();

                responseList.add(responseDto);
            }
        }
        return responseList;
    }

    @Override
    public CxQuotationResponseDto updateQuotation(CxQuotationRequestDto dto) {
        return null;
    }

    @Override
    public List<CxQuotationResponseDto> getMyQuotation(CxQuotationRequestDto dto) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        CustomerRegistration customer = customerRegistrationRepo.findByUserEmailOne(currentEmail)
                .orElseThrow(() -> new RuntimeException("No customer found for email: " + currentEmail));

        String customerId = customer.getUserid();
        List<CxMaterialQuotationRequestHeader> headers = cxMaterialRequestHeaderRepository.findByCustomerId(customerId);

        List<CxQuotationResponseDto> responseList = new ArrayList<>();

        for (CxMaterialQuotationRequestHeader header : headers) {

            List<CxMaterialQuotationRequestHeaderDetails> detailsEntityList =
                    cxQuotationHeaderDetailRepository.findByQuotationRequestLineIdStartingWith(header.getMaterialRequestId());

            CxQuotationResponseDto.CxQuotationHeader responseHeader = CxQuotationResponseDto.CxQuotationHeader.builder()
                    .materialRequestId(header.getMaterialRequestId())
                    .requestDate(header.getRequestDate())
                    .requestStatus(header.getRequestStatus())
                    .materialRequestRequestedBy(header.getMaterialRequestRequestedBy())
                    .build();

            List<CxQuotationResponseDto.CxQuotationHeaderDetail> responseDetails = detailsEntityList.stream()
                    .map(detail -> CxQuotationResponseDto.CxQuotationHeaderDetail.builder()
                            .quotationRequestLineId(detail.getQuotationRequestLineId())
                            .quotationId(detail.getQuotationId())
                            .productCategory(detail.getProductCategory())
                            .productSubCategory(detail.getProductSubCategory())
                            .brand(detail.getBrand())
                            .sku(detail.getSku())
                            .productName(detail.getProductName())
                            .quantity(detail.getQuantity())
                            .build())
                    .collect(Collectors.toList());

            // Fetch customer details for getMyQuotation as well
            CxQuotationResponseDto.CustomerDetails customerDetails = null;
            if (header.getUpdatedBy() != null && !header.getUpdatedBy().isEmpty()) {
                CustomerRegistration cust = customerRegistrationRepo.findByUserid(header.getUpdatedBy());
                if (cust != null) {
                    customerDetails = CxQuotationResponseDto.CustomerDetails.builder()
                            .name(cust.getCustomerName())
                            .email(cust.getUserEmail())
                            .mobile(cust.getUserMobile())
                            .userId(cust.getUserid())
                            .userType(cust.getUserType())
                            .regSource(cust.getRegSource())
                            .build();
                }
            }

            CxQuotationResponseDto responseDto = CxQuotationResponseDto.builder()
                    .updatedBy(header.getUpdatedBy())
                    .updatedDate(header.getUpdatedDate())
                    .expectedDeliveryDate(header.getExpectedDeliveryDate())
                    .deliveryLocation(header.getDeliveryLocation())
                    .pincode(header.getPincode())
                    .headerlist(List.of(responseHeader))
                    .headerDetail(responseDetails)
                    .customerDetails(customerDetails)
                    .build();

            responseList.add(responseDto);
        }
        return responseList;
    }

    private int getNextSequenceNumber(String basePrefix) {
        return cxQuotationRepository.findLastMaterialRequestId(basePrefix)
                .map(lastId -> {
                    try {
                        String[] parts = lastId.split("_");
                        return Integer.parseInt(parts[1]) + 1;
                    } catch (Exception e) {
                        return 1;
                    }
                })
                .orElse(1);
    }
}