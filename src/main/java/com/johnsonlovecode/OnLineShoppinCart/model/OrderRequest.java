package com.johnsonlovecode.OnLineShoppinCart.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderRequest {

    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String mobileNo;
    private String State;
    private String city;
    private String pincode;
    private String paymentType;
}
