package com.johnsonlovecode.OnLineShoppinCart.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {

    IN_PROGRESS(1, "In Progress"),
    ORDER_RECEIVED(2, "Order Received"),
    PRODUCT_PACKED(3, "Product Packed"),
    OUT_FOR_DELIVERED(4, "Out for Delivered"),
    DELIVERED(5,"Delivered"),
    CANCEL(6, "Cancelled");

    private int id;
    private String name;



}
