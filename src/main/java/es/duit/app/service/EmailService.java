package es.duit.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailActivacion(String email, String nombre, String tokenActivacion) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(email);
        mensaje.setSubject("Activa tu cuenta en Duit");

        String textoEmail = String.format(
                "Hola %s,\n\n" +
                        "¡Gracias por registrarte en Duit!\n\n" +
                        "Para activar tu cuenta, haz clic en el siguiente enlace:\n" +
                        "%s/activate?token=%s\n\n" +
                        "Este enlace expirará en 24 horas.\n\n" +
                        "Si no te registraste en Duit, ignora este mensaje.\n\n" +
                        "Saludos,\n" +
                        "El equipo de Duit",
                nombre, baseUrl, tokenActivacion);

        mensaje.setText(textoEmail);
        mensaje.setFrom("noreply@duit.es");

        mailSender.send(mensaje);
    }
}