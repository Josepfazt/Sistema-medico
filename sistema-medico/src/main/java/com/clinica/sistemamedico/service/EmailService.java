package com.clinica.sistemamedico.service;

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

    public void enviarCorreoBienvenida(String correoDestino, String nombreUsuario) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(correoDestino);
            helper.setSubject("¡Bienvenido a Nuestra Clínica! Gracias por tu confianza");

            // Diseño HTML limpio, moderno y elegante
            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 8px; padding: 20px; color: #334155;'>"
                    + "<div style='text-align: center; margin-bottom: 20px;'>"
                    + "<h1 style='color: #0284c7; margin: 0;'>Clínica Salud Integral</h1>"
                    + "</div>"
                    + "<h2 style='color: #1e293b;'>¡Hola, " + nombreUsuario + "!</h2>"
                    + "<p style='line-height: 1.6;'>Queremos agradecerte sinceramente por registrarte en nuestra plataforma y depositar tu confianza en nosotros. Estamos comprometidos con brindarte la mejor atención médica y una experiencia digital sin fricciones.</p>"
                    + "<p style='line-height: 1.6;'>A partir de este momento, tu cuenta de paciente se encuentra totalmente activa. Podrás agendar tus citas en línea de forma rápida y segura.</p>"
                    + "<div style='text-align: center; margin: 30px 0;'>"
                    + "<a href='http://localhost:8080/login.html' style='background-color: #0284c7; color: white; padding: 12px 24px; text-decoration: none; font-weight: bold; border-radius: 6px; display: inline-block; box-shadow: 0 4px 6px -1px rgba(2, 132, 199, 0.4);'>Ir al Portal Médico</a>"
                    + "</div>"
                    + "<p style='font-size: 0.875rem; color: #64748b; line-height: 1.6;'>Si no solicitaste este registro, por favor ignora este correo de forma segura.</p>"
                    + "<hr style='border: 0; border-top: 1px solid #e2e8f0; margin: 20px 0;'>"
                    + "<p style='text-align: center; font-size: 0.75rem; color: #94a3b8;'>© 2026 Clínica Salud Integral. Todos los derechos reservados.</p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); // En producción usaríamos un Logger
        }
    }
}