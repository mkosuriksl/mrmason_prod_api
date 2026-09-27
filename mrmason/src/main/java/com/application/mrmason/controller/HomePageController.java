

package com.application.mrmason.controller;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.application.mrmason.dto.MaterialRequestsFilterDto;
import com.application.mrmason.entity.CustomerRetailerOrderHdrEntity;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.MaterialRequestsFilterDto;
import com.application.mrmason.entity.CustomerRetailerOrderHdrEntity;
import com.application.mrmason.service.CustomerOrderHandler;

@RestController
@RequestMapping("/api/home")
public class HomePageController {

    @Autowired
    private CustomerOrderHandler customerOrderHandler;

    @PostMapping("/material-requests-by-customer/search")
    public ResponseEntity<List<CustomerRetailerOrderHdrEntity>> findMaterialRequestsByFilters(
            @RequestBody MaterialRequestsFilterDto filterDto) {

        List<CustomerRetailerOrderHdrEntity> result =
                customerOrderHandler.findMaterialRequestsByFilters(filterDto);

        return ResponseEntity.ok(result);
    }
}



