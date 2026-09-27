package com.application.mrmason.controller;

import com.application.mrmason.dto.CxQuotationRequestDto;
import com.application.mrmason.dto.CxQuotationResponseDto;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.service.impl.CxQuotationServiceImpl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quotation")
public class CxQuotationController {

    private final CxQuotationServiceImpl cxQuotationService;


    @PostMapping("/create")
    public ResponseEntity<GenericResponse<List<CxQuotationResponseDto>>>
                    createQuotation (@RequestBody List<CxQuotationRequestDto> dto){

        try {
            List<CxQuotationResponseDto> responseData = cxQuotationService.createQuotation(dto);

            GenericResponse<List<CxQuotationResponseDto>> response = GenericResponse.<List<CxQuotationResponseDto>>builder()
                    .message("Quotation request created successfully")
                    .success(true)
                    .data(responseData)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception ex) {
            GenericResponse<List<CxQuotationResponseDto>> errorResponse = GenericResponse.<List<CxQuotationResponseDto>>builder()
                    .message("Failed to create quotation: " + ex.getMessage())
                    .success(false)
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /*--------------------------------------Get my quotation (logged in user)----------------------------------------*/

    @GetMapping("/get_my_quote_request")
    public ResponseEntity<GenericResponse<List<CxQuotationResponseDto>>> getMyQuotations(CxQuotationRequestDto dto){
        try {
            List<CxQuotationResponseDto> responseData = cxQuotationService.getMyQuotation(dto);

            GenericResponse<List<CxQuotationResponseDto>> response = GenericResponse.<List<CxQuotationResponseDto>>builder()
                    .message("Fetched quotation request successfully")
                    .success(true)
                    .data(responseData)
                    .build();
            return ResponseEntity.ok(response);
        }catch (Exception ex) {
            GenericResponse<List<CxQuotationResponseDto>> errorResponse = GenericResponse.<List<CxQuotationResponseDto>>builder()
                    .message("Failed to fetch quotations request: " + ex.getMessage())
                    .success(false)
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /*-------------------------------------update my quotation (logged in user)-------------------------------------*/

    @PutMapping("/update-by_user")
    public ResponseEntity<GenericResponse<CxQuotationResponseDto>> updateQuotationById(@RequestBody CxQuotationRequestDto dto){

        CxQuotationResponseDto responseData = cxQuotationService.updateQuotation(dto);

        try{
            GenericResponse<CxQuotationResponseDto> response = GenericResponse.<CxQuotationResponseDto>builder()
                    .message("Quotation updated successfully")
                    .data(responseData)
                    .success(true)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            GenericResponse<CxQuotationResponseDto> errorResponse = GenericResponse.<CxQuotationResponseDto>builder()
                    .message("Failed to update quotation: " + e.getMessage())
                    .success(false)
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("get_all_quotation_info")
    @PreAuthorize("hasAnyRole('Adm', 'MS')")
    public ResponseEntity<GenericResponse<List<CxQuotationResponseDto>>> getAllQuotations(String productCategory,
                                                                                          String productSubCategory,
                                                                                          String brand,
                                                                                          String stockKeepingUnit,
                                                                                          String productName,
                                                                                          String quantity,
                                                                                          String pincode,
                                                                                          String updatedBy,
                                                                                          String expectedDeliveryDate,
                                                                                          String updatedDate){
        try {
            List<CxQuotationResponseDto> responseData = cxQuotationService.getAllQuotation
                    (productCategory, productSubCategory , brand, stockKeepingUnit, productName,
                            quantity, pincode, updatedBy, expectedDeliveryDate, updatedDate);

            GenericResponse<List<CxQuotationResponseDto>> response = GenericResponse.<List<CxQuotationResponseDto>>builder()
                    .message("Fetched quotation successfully")
                    .success(true)
                    .data(responseData)
                    .build();
            return ResponseEntity.ok(response);
        }catch (Exception ex) {
            GenericResponse<List<CxQuotationResponseDto>> errorResponse = GenericResponse.<List<CxQuotationResponseDto>>builder()
                    .message("Failed to fetch quotations: " + ex.getMessage())
                    .success(false)
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}