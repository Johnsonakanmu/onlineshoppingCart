package com.johnsonlovecode.OnLineShoppinCart.controller;

import com.johnsonlovecode.OnLineShoppinCart.model.Category;
import com.johnsonlovecode.OnLineShoppinCart.model.Product;
import com.johnsonlovecode.OnLineShoppinCart.model.ProductOrder;
import com.johnsonlovecode.OnLineShoppinCart.model.UserDtls;
import com.johnsonlovecode.OnLineShoppinCart.service.*;
import com.johnsonlovecode.OnLineShoppinCart.util.OrderStatus;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private CategoryService categoryService;
    private ProductService productService;
    private UserDtlsService userDtlsService;
    private CartService cartService;
    private OrderService orderService;

    @GetMapping("/")
    public String index(){
        return "admin/index";
    }

    // we load all category from all load add product
    @GetMapping("/loadAddProduct")
    public String loadProduct(Model m){
        List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("categories", categories);
        return "admin/add_product";
    }

    // List all Category REST Endpoint API
    @GetMapping("/category")
    public String category(Model m){
        m.addAttribute("categorys", categoryService.getAllCategory());
        return "admin/category";
    }

    // Save Category REST ApI
    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
        category.setImageName(imageName);

        Boolean existsCategory = categoryService.existCategory(category.getName());

        if(existsCategory){
            redirectAttributes.addFlashAttribute("errorMsg", "Category Name already exist");
       }else {
            Category saveCategory = categoryService.saveCategory(category);
            if(ObjectUtils.isEmpty(saveCategory)){
                redirectAttributes.addFlashAttribute("errorMage", "Failed to save category");
            }else {

                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+ File.separator+file.getOriginalFilename());

//                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                redirectAttributes.addFlashAttribute("successMsg", "Saved successfully");
            }
        }

        return "redirect:/admin/category";
    }

    // Delete Category Rest API
    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable("id") int categoryId, RedirectAttributes redirectAttributes){

       Boolean deleteCategory = categoryService.deleteCategory(categoryId);
        if(deleteCategory){
            redirectAttributes.addFlashAttribute("errorMsg", "Category Deleted Successfully");
        }else {
            redirectAttributes.addFlashAttribute("errorMsg", "Something went wrong on the server");
        }

        return "redirect:/admin/category";

    }

    // Load Edit Category Rest API
    @GetMapping("/loadEditCategory/{id}")
    public  String loadEditCategory(@PathVariable("id") int categoryId, Model m){

        m.addAttribute("category", categoryService.getCategoryById(categoryId));
        return "admin/edit_category";
    }

    // Save Update Category Rest API
    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file")
                            MultipartFile file, RedirectAttributes redirectAttributes ) throws IOException {

        Category oldCategory = categoryService.getCategoryById(category.getId());
        String imageName = (file != null && !file.isEmpty()) ? file.getOriginalFilename() : oldCategory.getImageName();

        if(!ObjectUtils.isEmpty(category)){
            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            oldCategory.setImageName(imageName);

        }

        if(!file.isEmpty()){
            File saveFile = new ClassPathResource("static/img").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+ File.separator+file.getOriginalFilename());

            System.out.println(path);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }

       Category updateCategory = categoryService.saveCategory(oldCategory);

        if(!ObjectUtils.isEmpty(updateCategory)){
            redirectAttributes.addFlashAttribute("successMsg", "Category update Successfully");
        }else {
            redirectAttributes.addFlashAttribute("errorMsg", "Something went wrong on the server");

        }

        return "redirect:/admin/loadEditCategory/" + category.getId();
    }


    //  Add Product End Point Rest API
    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image, RedirectAttributes redirectAttributes) throws IOException {

        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());

        Product saveProduct = productService.saveProduct(product);

        if (!ObjectUtils.isEmpty(saveProduct)){

            File saveFile = new ClassPathResource("static/img").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"product_img"+ File.separator+image.getOriginalFilename());

            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            redirectAttributes.addFlashAttribute("successMsg", "Product save Successfully");
        }else {
            redirectAttributes.addFlashAttribute("errorMsg", "Something went wrong on the server");

        }

        return "redirect:/admin/loadAddProduct";
    }

    // End point to Load View Products Rest API
    @GetMapping("/products")
    public String loadViewProduct(Model m){

        m.addAttribute("products", productService.getAllProducts());
        return "/admin/products";
    }

    // End point to Delete Products Rest API
    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable("id") int productId, RedirectAttributes redirectAttributes){
      Boolean deleteProduct =  productService.deleteProduct(productId);

      if (deleteProduct){
          redirectAttributes.addFlashAttribute("successMsg", "Product Deleted Successfully");
      }else {
          redirectAttributes.addFlashAttribute("errorMsg", "Product not found or could not be deleted");
      }
        return "redirect:/admin/products";
    }

    // Get Update product End Point

    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id, Model m){

        m.addAttribute("product", productService.getProductById(id));
        m.addAttribute("category"); categoryService.getCategoryById(id);
        return "admin/edit_product";

    }

    // Update Product End Point
    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image, RedirectAttributes redirectAttributes){

         if (product.getDiscount() < 0 || product.getDiscount() > 100){
             redirectAttributes.addFlashAttribute("errorMsg", "Invalid Discount");
         }else {
             Product updateProduct =   productService.updateProduct(product, image);
             if (!ObjectUtils.isEmpty(updateProduct)){
                 redirectAttributes.addFlashAttribute("successMsg", "Product update Successfully");
             }else {
                 redirectAttributes.addFlashAttribute("errorMsg", "Something went wrong on the server");
             }
         }
        return "redirect:/admin/editProduct/" + product.getId();
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

    //Get all User Endpoint
    @GetMapping("/users")
    public String getAllUser(Model m){
       List<UserDtls> user =  userDtlsService.getUsers("ROLE_USER");
        m.addAttribute("users", user );
        return "/admin/users";
    }

    // To Update user account To Active and Inactive
    @GetMapping("/updateStatus")
    public String updateUserAccountStatus(@RequestParam Boolean status,
                                          @RequestParam Integer id,
                                          RedirectAttributes redirectAttributes) {

        Boolean f = userDtlsService.updateAccountStatus(id, status);

        if (f) {
            if (status) {
                redirectAttributes.addFlashAttribute("successMsg", "User account activated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("successMsg", "User account deactivated successfully!");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Something went wrong while updating the account status.");
        }

        return "redirect:/admin/users";
    }

    //Get all User Endpoint
    @GetMapping("/orders")
    public String getAllOrders(Model m){

       List<ProductOrder> allOrders = orderService.getAllOrders();

       m.addAttribute("orders", allOrders);

        return "/admin/orders";
    }


    @PostMapping("/update-order-status")
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

        return "redirect:/admin/orders";
    }





}
