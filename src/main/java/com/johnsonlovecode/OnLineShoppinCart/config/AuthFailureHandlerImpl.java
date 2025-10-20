package com.johnsonlovecode.OnLineShoppinCart.config;

import com.johnsonlovecode.OnLineShoppinCart.model.UserDtls;
import com.johnsonlovecode.OnLineShoppinCart.respository.UserDtlsRepository;
import com.johnsonlovecode.OnLineShoppinCart.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    private final UserDtlsRepository userDtlsRepository;

    {
        this.setDefaultFailureUrl("/signin?error");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("username");
        UserDtls userDtls = userDtlsRepository.findByEmail(email);

        if (userDtls != null) {
            if (userDtls.getIsEnable()) {

                if (!userDtls.getAccountNonLocked()) {
                    // Account is currently locked
                    if (userDtls.getLockTime() != null) {
                        long lockTimeInMillis = userDtls.getLockTime().getTime();
                        long unlockDuration = AppConstant.UNLOCK_DURATION_TIME; // in milliseconds

                        if (System.currentTimeMillis() - lockTimeInMillis > unlockDuration) {
                            // ✅ Unlock account
                            userDtls.setAccountNonLocked(true);
                            userDtls.setFailedAttempted(0);
                            userDtls.setLockTime(null);
                            userDtlsRepository.save(userDtls);
                            exception = new LockedException("Your account is now unlocked. Please try to login.");
                        } else {
                            // ⏳ Still locked
                            exception = new LockedException("Your account is Locked. Please try again later.");
                        }
                    } else {
                        exception = new LockedException("Your account is Locked. Please try again later.");
                    }

                } else {
                    // ✅ Account is not locked yet → Handle failed attempts properly
                    int failedAttempts = userDtls.getFailedAttempted() + 1;
                    userDtls.setFailedAttempted(failedAttempts);

                    if (failedAttempts >= AppConstant.ATTEMPT_TIME) {
                        // 🚫 Lock account after 3rd failed attempt
                        userDtls.setAccountNonLocked(false);
                        userDtls.setLockTime(new Date());
                        exception = new LockedException("Your account is Locked due to 3 failed login attempts.");
                    } else {
                        // ⚠️ Still allowed to retry
                        exception = new BadCredentialsException("Invalid credentials. Attempt "
                                + failedAttempts + " of " + AppConstant.ATTEMPT_TIME);
                    }

                    userDtlsRepository.save(userDtls);
                }

            } else {
                exception = new LockedException("Your account is Inactive");
            }
        } else {
            exception = new BadCredentialsException("Email & password is invalid");
        }

        request.getSession().setAttribute("error_message", exception.getMessage());
        super.onAuthenticationFailure(request, response, exception);
    }
}





