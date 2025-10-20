package com.johnsonlovecode.OnLineShoppinCart.config;
// B
import com.johnsonlovecode.OnLineShoppinCart.model.UserDtls;
import com.johnsonlovecode.OnLineShoppinCart.respository.UserDtlsRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserDtlsRepository userDtlsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

       UserDtls user = userDtlsRepository.findByEmail(username);

       if (user == null){
           throw new UsernameNotFoundException("User Not Found");
       }
        return new CustomUser(user);
    }
}
