package com.johnsonlovecode.OnLineShoppinCart.service.Impl;

import com.johnsonlovecode.OnLineShoppinCart.model.Cart;
import com.johnsonlovecode.OnLineShoppinCart.model.Product;
import com.johnsonlovecode.OnLineShoppinCart.model.UserDtls;
import com.johnsonlovecode.OnLineShoppinCart.respository.CartRepository;
import com.johnsonlovecode.OnLineShoppinCart.respository.ProductRepository;
import com.johnsonlovecode.OnLineShoppinCart.respository.UserDtlsRepository;
import com.johnsonlovecode.OnLineShoppinCart.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CartServerImpl implements CartService {

    private CartRepository cartRepository;
    private UserDtlsRepository userDtlsRepository;
    private ProductRepository productRepository;

    @Override
    public Cart saveCart(Integer productId, Integer userId) {

        UserDtls userDtls = userDtlsRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();

        Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

        Cart cart = null;
        if (ObjectUtils.isEmpty(cartStatus)){
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(userDtls);
            cart.setQuantity(1);
            cart.setTotalPrice(1 * product.getDiscountPrice());
        }else {
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() +1);
            cart.setTotalPrice(cart.getQuantity()* cart.getProduct().getDiscountPrice());
        }
        Cart saveCart = cartRepository.save(cart);
        System.out.println();
        return saveCart;
    }

    @Override
    public List<Cart> getCartsByUser(Integer userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);

        Double totalOrderPrice = 0.0;
        List<Cart> updateCarts = new ArrayList<>();

        for (Cart c: carts){
            Double totalPrice = (c.getProduct().getDiscountPrice() * c.getQuantity());
            c.setTotalPrice(totalPrice);

            totalOrderPrice = totalOrderPrice + totalPrice;
            c.setTotalOrderPrice(totalOrderPrice);
            updateCarts.add(c);
        }
        return updateCarts;
    }

    @Override
    public Integer getCountCart(Integer userId) {

       Integer countByUserId = cartRepository.countByUserId(userId);

        return countByUserId;
    }

    // logic on ho decrease and increase product purchase

    @Override
    public void updateQuantity(String sy, Integer cid) {
        Cart cart = cartRepository.findById(cid).get();
        int updateQuantity;

        if (sy.equalsIgnoreCase("de")){
            updateQuantity =cart.getQuantity() - 1;

            if (updateQuantity <= 0){
                cartRepository.delete(cart);
            }
            else {
                cart.setQuantity(updateQuantity);
                cartRepository.save(cart);
            }
        }else {
            updateQuantity = cart.getQuantity()+ 1;
            cart.setQuantity(updateQuantity);
            cartRepository.save(cart);
        }

    }

}
