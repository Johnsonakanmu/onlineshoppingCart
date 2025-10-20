package com.johnsonlovecode.OnLineShoppinCart.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "productOrders")
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String orderId;
    private Date orderDate;
    private Double price;
    @ManyToOne
    private Product product;
    @ManyToOne
    private UserDtls user;
    private Integer quantity;
    private String status;
    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress orderAddress;
    private String  paymentType;
    @CreationTimestamp
    private LocalDateTime createDated;
    @UpdateTimestamp
    private LocalDateTime lastUpdated;

}
