package com.johnsonlovecode.OnLineShoppinCart.controller;

import com.johnsonlovecode.OnLineShoppinCart.model.Cart;
import com.johnsonlovecode.OnLineShoppinCart.model.Category;
import com.johnsonlovecode.OnLineShoppinCart.model.Product;
import com.johnsonlovecode.OnLineShoppinCart.model.UserDtls;
import com.johnsonlovecode.OnLineShoppinCart.service.CartService;
import com.johnsonlovecode.OnLineShoppinCart.service.CategoryService;
import com.johnsonlovecode.OnLineShoppinCart.service.ProductService;
import com.johnsonlovecode.OnLineShoppinCart.service.UserDtlsService;
import com.johnsonlovecode.OnLineShoppinCart.util.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class HomeController {

    private CategoryService categoryService;

    private ProductService productService;

    private UserDtlsService userDtlsService;

    private CommonUtil commonUtil;

    private BCryptPasswordEncoder passwordEncoder;

    private CartService cartService;

    @GetMapping("/")
    public String index(){
        return "index";
    }

    // We change the login to signin
    @GetMapping("/signin")
    public String login(){
        return "login";
    }


    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/products")
    public String products(Model m, @RequestParam(value = "category", defaultValue = "") String  category){
        List<Category> categories = categoryService.getAllActiveCategory();
        List<Product> products = productService.getAllActiveProducts(category);
        m.addAttribute("categories", categories);
        m.addAttribute("products", products);
        m.addAttribute("paramValve", category);
        return "product";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable("id") int id, Model m){
        Product productById = productService.getProductById(id);
        m.addAttribute("product", productById);
        return "view_product";
    }


    // Save User Rest API
    @PostMapping("/saveUser")
    public  String SaveUsed(@ModelAttribute UserDtls user, @RequestParam("img")
                            MultipartFile file, RedirectAttributes attributes) throws IOException {

        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setProfileImage(imageName);

       UserDtls saveUser = userDtlsService.savedUserDetail(user);

        if (!ObjectUtils.isEmpty(saveUser)) {

            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            attributes.addFlashAttribute("successMsg", "Register successfully!");
        } else {
            attributes.addFlashAttribute("errorMsg", "Something went wrong on the server.");
        }

        return "redirect:/register";
    }


    // We use the under the base Html just to get the name dynamical
    @ModelAttribute
    public void getUserDetails(Principal p, Model m){
        if (p != null){
            String email = p.getName();
            UserDtls userDtls = userDtlsService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
            Integer countCart = cartService.getCountCart(userDtls.getId());
            m.addAttribute("countCart", countCart);
        }

        List<Category> allActiveCategories =  categoryService.getAllActiveCategory();
        m.addAttribute("categorys", allActiveCategories);
    }


    // Forget Password End point
    @GetMapping("/forgot-password")
    public String showForgetPassword(){
        return "/forgot_password";
    }


    @PostMapping("/forgot-password")
    public String processForgetPassword(@RequestParam String email, RedirectAttributes redirectAttributes, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        UserDtls userByEmail = userDtlsService.getUserByEmail(email);

        if (ObjectUtils.isEmpty(userByEmail)) {
            redirectAttributes.addFlashAttribute("errorMsg", "Invalid email");
        } else {
            String resetToken = UUID.randomUUID().toString();
            userDtlsService.updateUserResetToken(email, resetToken);

            String url = CommonUtil.generateTrl(request) + "/reset-password?token=" + resetToken;

            boolean sendEmail = commonUtil.sendEmail(url, email); // Note: use instance, not class
            if (sendEmail) {
                redirectAttributes.addFlashAttribute("successMsg", "Please check your email... Password Reset link sent");
            } else {
                redirectAttributes.addFlashAttribute("errorMsg", "Something went wrong on the server | Email not sent");
            }
        }
        return "redirect:/forgot-password";
    }


    // Reset Password End point
    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String token, Model m, RedirectAttributes redirectAttributes){

        UserDtls userByToken = userDtlsService.getUserByToken(token);

        if (userByToken == null){
            m.addAttribute("msg", "Your link is invalid or expired ||");
            return "message";
        }
        m.addAttribute("token", token);

        return "/reset_password";
    }


    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String password, Model m, RedirectAttributes redirectAttributes){

        UserDtls userByToken = userDtlsService.getUserByToken(token);

        if (userByToken == null){
            m.addAttribute("errorMsg", "Your link is invalid or expired ||");
            return "message";
        }else {
            userByToken.setPassword(passwordEncoder.encode(password));
            userByToken.setResetToken(null);
            userDtlsService.updateUser(userByToken);
            redirectAttributes.addFlashAttribute("successMsg", "Password change successfully");
            m.addAttribute("msg", "Password change successfully");
            return "message";

        }

    }


    @GetMapping("/addCart")
    public String addCart(@RequestParam Integer pid, @RequestParam Integer uid, RedirectAttributes attributes){

        Cart sveCart = cartService.saveCart(pid, uid);

        if (ObjectUtils.isEmpty(sveCart)){
            attributes.addFlashAttribute("errorMsg", "Product added to cart failed");
        }else {
            attributes.addFlashAttribute("successMsg", "Product added to cart successfully");

        }

        return "redirect:/product/" + pid;
    }



}
