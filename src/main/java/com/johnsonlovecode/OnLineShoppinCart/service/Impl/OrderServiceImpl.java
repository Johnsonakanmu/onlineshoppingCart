package com.johnsonlovecode.OnLineShoppinCart.service.Impl;

import com.johnsonlovecode.OnLineShoppinCart.model.Cart;
import com.johnsonlovecode.OnLineShoppinCart.model.OrderAddress;
import com.johnsonlovecode.OnLineShoppinCart.model.OrderRequest;
import com.johnsonlovecode.OnLineShoppinCart.model.ProductOrder;
import com.johnsonlovecode.OnLineShoppinCart.respository.CartRepository;
import com.johnsonlovecode.OnLineShoppinCart.respository.ProductOrderRepository;
import com.johnsonlovecode.OnLineShoppinCart.service.OrderService;
import com.johnsonlovecode.OnLineShoppinCart.util.OrderStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private ProductOrderRepository orderRepository;
    private CartRepository cartRepository;


    @Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) {

       List<Cart> carts = cartRepository.findByUserId(userId);

       for(Cart cart: carts){
            ProductOrder order = new ProductOrder();

            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(new Date());

            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct ().getDiscountPrice());

            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());

            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());


           OrderAddress address = new OrderAddress();

           address.setFirstName(orderRequest.getFirstName());
           address.setLastName(orderRequest.getLastName());
           address.setEmail(orderRequest.getEmail());
           address.setMobileNo(orderRequest.getMobileNo());
           address.setAddress(orderRequest.getAddress());
           address.setCity(orderRequest.getCity());
           address.setState(orderRequest.getState());
           address.setPincode(orderRequest.getPincode());

           order.setOrderAddress(address);

           orderRepository.save(order);
       }
    }

    @Override
    public List<ProductOrder> getOrdersByUser(Integer userId) {
       List<ProductOrder> orders = orderRepository.findByUserId(userId);
        return orders;
    }

    @Override
    public Boolean updateOrderStatus(Integer id, String status) {

        Optional<ProductOrder> findById = orderRepository.findById(id);

        if (findById.isPresent()){
            ProductOrder productOrders = findById.get();
            productOrders.setStatus(status);
            orderRepository.save(productOrders);
            return true;
        }

        return false;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return orderRepository.findAll();
    }
}
