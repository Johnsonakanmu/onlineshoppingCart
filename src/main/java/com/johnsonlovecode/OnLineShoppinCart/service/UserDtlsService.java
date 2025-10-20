package com.johnsonlovecode.OnLineShoppinCart.service;

import com.johnsonlovecode.OnLineShoppinCart.model.UserDtls;

import java.util.List;

public interface UserDtlsService {

    public UserDtls savedUserDetail(UserDtls user);

    public UserDtls getUserByEmail(String email);

    public List<UserDtls> getUsers(String role);

    Boolean updateAccountStatus(Integer id, Boolean status);

    public void  increaseFailedAttempt(UserDtls user);

    public  void userAccountLock(UserDtls user);

    public boolean unlockAccountTimeExpired(UserDtls user);
    public void resetAttempt(int userId);
    public  void updateUserResetToken(String email, String resetToken);

    public  UserDtls getUserByToken(String token);

    public UserDtls updateUser(UserDtls user);




}
