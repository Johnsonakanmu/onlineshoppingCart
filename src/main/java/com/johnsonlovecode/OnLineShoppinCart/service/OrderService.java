package com.johnsonlovecode.OnLineShoppinCart.service;

import com.johnsonlovecode.OnLineShoppinCart.model.OrderRequest;
import com.johnsonlovecode.OnLineShoppinCart.model.ProductOrder;

import java.util.List;

public interface OrderService {

    public void saveOrder(Integer userId, OrderRequest orderRequest);

    public List<ProductOrder> getOrdersByUser(Integer userId);

}
