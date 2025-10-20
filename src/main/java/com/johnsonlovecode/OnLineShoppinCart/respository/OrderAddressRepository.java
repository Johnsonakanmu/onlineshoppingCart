package com.johnsonlovecode.OnLineShoppinCart.respository;

import com.johnsonlovecode.OnLineShoppinCart.model.OrderAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderAddressRepository extends JpaRepository<OrderAddress, Integer> {
}
