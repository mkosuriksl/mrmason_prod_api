package com.application.mrmason.dto;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.CustomerOrderDetailsEntity;
import com.application.mrmason.entity.CustomerOrderHdrEntity;

public interface CustomerOrderDetailsRepo extends JpaRepository<CustomerOrderDetailsEntity, String> {
	Optional<CustomerOrderDetailsEntity> findByOrderlineId(String orderLineId);
	@Query("SELECT d FROM CustomerOrderDetailsEntity d WHERE d.customerOrderOrderHdrEntity.orderId = :orderId")
	List<CustomerOrderDetailsEntity> findByOrderId(@Param("orderId") String orderId);
	
	@Query("SELECT d FROM CustomerOrderDetailsEntity d " +
		       "WHERE d.orderlineId = :orderlineId " +
		       "AND d.customerOrderOrderHdrEntity.orderId = :orderId")
		Optional<CustomerOrderDetailsEntity> findByOrderlineIdAndOrderId(
		        @Param("orderlineId") String orderlineId,
		        @Param("orderId") String orderId
		);

    List<CustomerOrderDetailsEntity> findByCustomerOrderOrderHdrEntity_UpdatedBy(String updatedBy);


@Query("""
    SELECT c
    FROM CustomerOrderHdrEntity c
    LEFT JOIN FETCH c.customerOrderDetailsEntities
    WHERE c.updatedBy = :updatedBy
      AND c.status = 0
""")
Optional<CustomerOrderHdrEntity> findActiveCartByUpdatedBy(
        @Param("updatedBy") String updatedBy
);






}
