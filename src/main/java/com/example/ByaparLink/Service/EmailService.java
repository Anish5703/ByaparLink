package com.example.ByaparLink.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

@Autowired
private JavaMailSender mailSender;

public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException
{
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message,true);

    helper.setTo(to);
    helper.setFrom("info@byaparlink.com");
    helper.setSubject(subject);
    helper.setText(htmlContent,true);
    mailSender.send(message);
}

//Custom html mail for confirmation
    public String buildConfirmationEmail(String username, String confirmationLink) {
        return String.format("""
        <p>Hi %s,</p>
        <p>Click below to confirm your email:</p>
        <p><a href="%s" style="color: white; background: #28a745; padding: 10px 15px; text-decoration: none; border-radius: 5px;">Confirm Email</a></p>
        <p>If you didn't request this, ignore this email.</p>
    """, username, confirmationLink);
    }


}
