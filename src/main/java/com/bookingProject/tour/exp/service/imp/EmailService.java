package com.bookingProject.tour.exp.service.imp;

import com.bookingProject.tour.exp.service.IEmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    @Override
    public void sendEmail(String toUser, String subject, String htmlMessage) {
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toUser);
            helper.setSubject(subject);
            helper.setFrom("equipo1.rutaviva@gmail.com");
            helper.setText(htmlMessage, true);
            // Inserta una imagen en el mensaje
            //helper.addInline("imagen.png", new ClassPathResource("/imagen.png"));
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

