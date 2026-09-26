package com.application.mrmason.entity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.application.mrmason.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_to_retailer_order_hdr")
public class CustomerRetailerOrderHdrEntity {

    @Id
    private String orderId;
    
    private String customerCartOrderId;
    private String customerId;
    private String retailerId;
    private String orderUpdatedBy;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDate orderDate;

    private Date orderUpdatedDate;
    
    @Column(name = "delivery_method")
    private String deliveryMethod;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus paymentStatus;

    @Column(name = "expected_delivery_date")
    private String expectedDeliveryDate;

    @Column(name = "delivery_location")
    private String deliveryLocation;

    @Column(name = "pincode")
    private String pincode;

    @OneToMany(mappedBy = "customerRetailerOrderHdr", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<CustomerRetailerOrderDetailsEntity> customerRetailerOrderDetailsList;



//    @PrePersist
//    private void prePersist() {
//        String sequenceNumber = generateRandomSixDigitNumber();
//        this.orderId = "INVOICE" + LocalDate.now().getYear() + sequenceNumber + "_" + this.retailerId;
//    }


//    private static final Random random = new Random();

//    private String generateRandomSixDigitNumber() {
//        // Generate a random integer between 100000 and 999999
//        int randomInt = random.nextInt(900000) + 100000;
//        return String.valueOf(randomInt);
//    }

}
