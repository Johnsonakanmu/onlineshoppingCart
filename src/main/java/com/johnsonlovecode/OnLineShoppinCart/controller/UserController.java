package com.johnsonlovecode.OnLineShoppinCart.controller;

import com.johnsonlovecode.OnLineShoppinCart.model.*;
import com.johnsonlovecode.OnLineShoppinCart.service.CartService;
import com.johnsonlovecode.OnLineShoppinCart.service.CategoryService;
import com.johnsonlovecode.OnLineShoppinCart.service.OrderService;
import com.johnsonlovecode.OnLineShoppinCart.service.UserDtlsService;
import com.johnsonlovecode.OnLineShoppinCart.util.OrderStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private UserDtlsService userDtlsService;
    private CategoryService categoryService;
    private CartService cartService;
    private OrderService orderService;

    @GetMapping("/")
    public String home(){
        return "user/home";
    }

    @ModelAttribute
    public void getUserDetails(Principal p, Model m){
        if(p != null){
            String email = p.getName();
            UserDtls userDtls = userDtlsService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
            Integer countCart = cartService.getCountCart(userDtls.getId());
            m.addAttribute("countCart", countCart);
        }

        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        m.addAttribute("categorys", allActiveCategory);
    }


    @GetMapping("/cart")
    public String loadCartPage(Principal p, Model m){

        UserDtls user = getLonggedUserDetails(p);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        m.addAttribute("carts", carts);

        if(carts.size() > 0) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }

        return "/user/cart";
    }

    private UserDtls getLonggedUserDetails(Principal p) {
        String email = p.getName();
        UserDtls userDtls = userDtlsService.getUserByEmail(email);
        return userDtls;
    }

    // logic on ho decrease and increase product purchase

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid){

        cartService.updateQuantity(sy,cid);
        return "redirect:/user/cart";
    }


    // Order endpoint
    @GetMapping("/orders")
    private String orderPage(Principal p, Model m){
        UserDtls user = getLonggedUserDetails(p);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        m.addAttribute("carts", carts);

        if(carts.size() > 0) {
            Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice()+ 200 + 100;
            m.addAttribute("orderPrice", orderPrice);
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/user/order";
    }


    @PostMapping("/save-orders")
    private String saveOrderPage(@ModelAttribute OrderRequest request, Principal p){

        UserDtls user = getLonggedUserDetails(p);
        orderService.saveOrder(user.getId(), request);

        return "/user/success";
    }


    @GetMapping("/success")
    public String loadSuccess(){
        return "/user/success";
    }


    @GetMapping("/user-orders")
    public String myOrder(Model m, Principal p){
        UserDtls loginUser = getLonggedUserDetails(p);
        List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());
        m.addAttribute("orders", orders);
        return "/user/my_orders";
    }


    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, RedirectAttributes attributes){

        OrderStatus[] values = OrderStatus.values();
        String status =null;

        for (OrderStatus orderSt : values){
            if(orderSt.getId() == st){
                status = orderSt.getName();
            }
        }
        Boolean updateOrder = orderService.updateOrderStatus(id, status);

        if (updateOrder){
             attributes.addFlashAttribute("successMsg", "Status Updated Successfully");
        }else {
            attributes.addFlashAttribute("errorMsg", "Status not updated");
        }

        return "redirect:/user/user-orders";
    }



}
