package com.johnsonlovecode.OnLineShoppinCart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "userDtls")
public class UserDtls {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String mobileNumber;
    private String email;
    private String address;
    private String  city;
    private String state;
    private String pincode;
    private String password;
    private String  profileImage;
    @CreationTimestamp
    private LocalDateTime dateCreated;
    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    // this role is for the security
    private String role;

    // is for also for security to check account isEnable
    private Boolean isEnable;
    // for accountNonLocked
    private Boolean accountNonLocked;
    // fail attempt (If u call more three time)
    private Integer failedAttempted;

    private Date lockTime;

    // for Email
    private String resetToken;

}
