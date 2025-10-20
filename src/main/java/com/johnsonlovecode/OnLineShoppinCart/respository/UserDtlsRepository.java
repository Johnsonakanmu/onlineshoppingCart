package com.johnsonlovecode.OnLineShoppinCart.respository;

import com.johnsonlovecode.OnLineShoppinCart.model.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDtlsRepository extends JpaRepository<UserDtls, Integer> {


    public UserDtls findByEmail(String email);

    List<UserDtls> findByRole(String role);

    public  UserDtls findByResetToken(String resetToken);



}
