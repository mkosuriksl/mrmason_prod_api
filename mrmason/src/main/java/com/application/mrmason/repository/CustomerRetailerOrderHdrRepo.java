package com.application.mrmason.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.CustomerRetailerOrderHdrEntity;
import com.application.mrmason.enums.OrderStatus;

public interface CustomerRetailerOrderHdrRepo extends JpaRepository<CustomerRetailerOrderHdrEntity, String> {

    Optional<CustomerRetailerOrderHdrEntity> findByOrderId(String orderId);

    List<CustomerRetailerOrderHdrEntity> findByCustomerId(String cId);

    List<CustomerRetailerOrderHdrEntity> findByCustomerIdIn(List<String> customerIdList);

	List<CustomerRetailerOrderHdrEntity> findByOrderDateBetween(LocalDate fromOrderDate, LocalDate toOrderDate);

	List<CustomerRetailerOrderHdrEntity> findByOrderStatus(OrderStatus orderStatus);

	List<CustomerRetailerOrderHdrEntity> findByRetailerId(String retailerId);


 // home page customer material requests query
    // @Query("""
    //     SELECT DISTINCT h
    //     FROM CustomerRetailerOrderHdrEntity h
    //     JOIN h.customerRetailerOrderDetailsList d
    //     WHERE d.materialCategory = :materialCategory
    //       AND d.materialSubCategory = :materialSubCategory
    //       AND d.brand = :brand
    //       AND d.updatedDate >= :postedDateFrom
    //       AND d.updatedDate < :postedDateTo
    //       AND h.expectedDeliveryDate >= :deliveryDateFrom
    //       AND h.expectedDeliveryDate < :deliveryDateTo
    //       AND h.deliveryLocation = :deliveryLocation
    //       AND h.orderStatus = :orderStatus
    //     """)
    // List<CustomerRetailerOrderHdrEntity> findMaterialRequestsByFilters(
    //         @Param("materialCategory") String materialCategory,
    //         @Param("materialSubCategory") String materialSubCategory,
    //         @Param("brand") String brand,
    //         @Param("postedDateFrom") Date postedDateFrom,
    //         @Param("postedDateTo") Date postedDateTo,
    //         @Param("deliveryDateFrom") LocalDate deliveryDateFrom,
    //         @Param("deliveryDateTo") LocalDate deliveryDateTo,
    //         @Param("deliveryLocation") String deliveryLocation,
    //         @Param("orderStatus") OrderStatus orderStatus);


    @Query("""
    SELECT DISTINCT h
    FROM CustomerRetailerOrderHdrEntity h
    JOIN h.customerRetailerOrderDetailsList d
    WHERE (d.materialCategory = :materialCategory)
      AND ( d.materialSubCategory = :materialSubCategory)
      AND ( d.brand = :brand)
      AND (:postedDateFrom IS NULL OR d.updatedDate >= :postedDateFrom)
      AND (:postedDateTo IS NULL OR d.updatedDate < :postedDateTo)
      AND (:deliveryDateFrom IS NULL OR h.expectedDeliveryDate >= :deliveryDateFrom)
      AND (:deliveryDateTo IS NULL OR h.expectedDeliveryDate < :deliveryDateTo)
      AND (:deliveryLocation IS NULL OR h.deliveryLocation = :deliveryLocation)
      AND (:orderStatus IS NULL OR h.orderStatus = :orderStatus)
    """)
List<CustomerRetailerOrderHdrEntity> findMaterialRequestsByFilters(
        @Param("materialCategory") String materialCategory,
        @Param("materialSubCategory") String materialSubCategory,
        @Param("brand") String brand,
        @Param("postedDateFrom") Date postedDateFrom,
        @Param("postedDateTo") Date postedDateTo,
        @Param("deliveryDateFrom") LocalDate deliveryDateFrom,
        @Param("deliveryDateTo") LocalDate deliveryDateTo,
        @Param("deliveryLocation") String deliveryLocation,
        @Param("orderStatus") OrderStatus orderStatus);








}
