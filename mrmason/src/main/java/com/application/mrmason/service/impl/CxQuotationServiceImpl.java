package com.application.mrmason.service.impl;

import com.application.mrmason.dto.CxQuotationRequestDto;
import com.application.mrmason.dto.CxQuotationResponseDto;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.CxQuotation;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.CxQuotationRepository;
import com.application.mrmason.security.JwtService;
import com.application.mrmason.service.CxQuotationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CxQuotationServiceImpl implements CxQuotationService {

    private final CxQuotationRepository cxQuotationRepository;
    private final CustomerRegistrationRepo customerRegistrationRepo;
    private final EmailServiceImpl emailService;
    private final JwtService jwtService;


    @Override
    public CxQuotationResponseDto createQuotation(CxQuotationRequestDto dto) {

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        CustomerRegistration customer = customerRegistrationRepo.findByUserEmailOne(currentEmail)
                .orElseThrow(()-> new RuntimeException("No customer found : " + currentEmail));

        if(dto == null){
            throw new RuntimeException("Details can't be null");
        }

        String loggedUser = customer.getUserid();

        String DateFormat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String uniqueId = "QT" + DateFormat;

        CxQuotation quotation = CxQuotation.builder()
                .requestId(uniqueId)
                .productName(dto.getProductName())
                .productCategory(dto.getProductCategory())
                .productSubCategory(dto.getProductSubCategory())
                .brand(dto.getBrand())
                .stockKeepingUnit(dto.getStockKeepingUnit())
                .quantity(dto.getQuantity())
                .expectedDeliveryDate(dto.getExpectedDeliveryDate())
                .DeliveryLocation(dto.getDeliveryLocation())
                .pincode(dto.getPincode())
                .updatedBy(loggedUser)
                .updatedDate(LocalDateTime.now())
                .build();

        CxQuotation savedQuotation = cxQuotationRepository.save(quotation);

        String subject = "Confirmed Quotation - " + savedQuotation.getProductName();
        String body = String.format(
                "Dear %s,<br><br>" +
                        "Your quotation for '%s' (Quantity: %s) has been successfully created." +
                        "<br><br>Thank You!",
                customer.getCustomerName(),
                savedQuotation.getProductName(),
                savedQuotation.getQuantity());
        emailService.sendEmail(customer.getUserEmail(), subject, body);

        return mapToDto(savedQuotation);
    }

    /*--------------------------------------Get my quotation (logged in user)----------------------------------------*/

    @Override
    public List<CxQuotationResponseDto> getMyQuotation(CxQuotationRequestDto dto) {

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        CustomerRegistration customer = customerRegistrationRepo.findByUserEmailOne(currentEmail)
                .orElseThrow(()-> new RuntimeException("No customer found : " + currentEmail));

        String customerId = customer.getUserid();

        List<CxQuotation> quotationList = cxQuotationRepository.findByUpdatedBy(customerId);

        return quotationList.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /*-------------------------------------update my quotation (logged in user)-------------------------------------*/

    @Override
    public CxQuotationResponseDto updateQuotation(CxQuotationRequestDto dto) {

        if (dto == null || dto.getRequestId() == null) {
            throw new RuntimeException("Request ID and details cannot be null");
        }

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        CustomerRegistration customer = customerRegistrationRepo.findByUserEmailOne(currentEmail)
                .orElseThrow(()-> new RuntimeException("No customer found : " + currentEmail));

        String customerId = customer.getUserid();

        CxQuotation quotation = cxQuotationRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new RuntimeException("No request id found : " + dto.getRequestId()));

        if(!customerId.equals(quotation.getUpdatedBy())){
            throw new RuntimeException("Unauthorized: You do not have permission to update this quotation");
        }

        quotation.setQuantity(dto.getQuantity());
        quotation.setDeliveryLocation(dto.getDeliveryLocation());
        quotation.setExpectedDeliveryDate(dto.getExpectedDeliveryDate());
        quotation.setPincode(dto.getPincode());
        quotation.setUpdatedBy(customerId);
        quotation.setUpdatedDate(LocalDateTime.now());

        CxQuotation updatedQuotation = cxQuotationRepository.save(quotation);

        return mapToDto(updatedQuotation);
    }

    @Override
    public List<CxQuotationResponseDto> getAllQuotation(String productCategory ,
                                                  String productSubCategory ,
                                                  String brand,
                                                  String stockKeepingUnit,
                                                  String productName,
                                                  String quantity,
                                                  String pincode,
                                                  String updatedBy,
                                                  String expectedDeliveryDate,
                                                  String updatedDate) {

        LocalDateTime parsedUpdatedDate = null;
        if (updatedDate != null && !updatedDate.trim().isEmpty()) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            parsedUpdatedDate = LocalDate.parse(updatedDate, formatter).atStartOfDay();
        }

        List<CxQuotation> quotationAllList = cxQuotationRepository.findByFilters(productCategory ,
                productSubCategory ,
                brand,
                stockKeepingUnit,
                productName,
                quantity,
                pincode,
                updatedBy,
                expectedDeliveryDate,
                parsedUpdatedDate);

        if (quotationAllList.isEmpty()) {
            throw new RuntimeException("No quotations found for the given criteria");
        }

        return quotationAllList.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    CxQuotationResponseDto mapToDto (CxQuotation cxQuotation) {
        return CxQuotationResponseDto.builder()
                .productName(cxQuotation.getProductName())
                .requestId(cxQuotation.getRequestId())
                .productCategory(cxQuotation.getProductCategory())
                .productSubCategory(cxQuotation.getProductSubCategory())
                .brand(cxQuotation.getBrand())
                .stockKeepingUnit(cxQuotation.getStockKeepingUnit())
                .quantity(cxQuotation.getQuantity())
                .expectedDeliveryDate(cxQuotation.getExpectedDeliveryDate())
                .DeliveryLocation(cxQuotation.getDeliveryLocation())
                .pincode(cxQuotation.getPincode())
                .updatedBy(cxQuotation.getUpdatedBy())
                .updatedDate(cxQuotation.getUpdatedDate())
                .build();
    }
}
