package com.application.mrmason.repository;

import com.application.mrmason.entity.CxMaterialQuotationRequestHeaderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.CustomerRetailerOrderDetailsEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CustomerRetailerOrderDetailsRepo extends JpaRepository<CustomerRetailerOrderDetailsEntity, String> {


    @Query("SELECT c FROM CustomerRetailerOrderDetailsEntity c WHERE c.customerRetailerOrderHdr.orderId = :orderId")
    List<CustomerRetailerOrderDetailsEntity> findByOrderId(@Param("orderId") String orderId);



}
