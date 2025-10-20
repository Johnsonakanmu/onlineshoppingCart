package com.johnsonlovecode.OnLineShoppinCart.util;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
@AllArgsConstructor
public class CommonUtil {

    private JavaMailSender mailSender;

    public boolean sendEmail(String url, String recipientEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("support@ibeacon.org", "Shopping Cart"); // Update this with a valid email
        helper.setTo(recipientEmail);

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + url + "\">Change my password</a></p>";

        helper.setSubject("Password Reset");
        helper.setText(content, true);

        mailSender.send(message);
        return true;
    }



    public static String generateTrl(HttpServletRequest request) {
        String scheme = request.getScheme(); // http or https
        String serverName = request.getServerName(); // e.g., localhost or domain.com
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath(); // e.g., /ShoppingCart

        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        // Include port only if it's not standard
        if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
            url.append(":").append(serverPort);
        }

        url.append(contextPath); // optional — keep if you're deploying under a context path

        return url.toString();
    }


}
