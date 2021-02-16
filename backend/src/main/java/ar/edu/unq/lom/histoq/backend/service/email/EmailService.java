package ar.edu.unq.lom.histoq.backend.service.email;

public interface EmailService {
    void sendSimpleMessage(String to, String cc, String subject, String text);
    void sendSimpleMessageAsync(String to, String cc, String subject, String text);
}
