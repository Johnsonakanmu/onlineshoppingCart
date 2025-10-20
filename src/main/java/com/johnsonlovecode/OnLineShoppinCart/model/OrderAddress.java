package com.johnsonlovecode.OnLineShoppinCart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orderAddress ")
public class OrderAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String mobileNo;
    private String state;
    private String city;
    private String pincode;
    @CreationTimestamp
    private LocalDateTime createDated;
    @UpdateTimestamp
    private LocalDateTime lastUpdated;
}
