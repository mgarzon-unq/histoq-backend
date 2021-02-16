package ar.edu.unq.lom.histoq.backend.service.email;

import ar.edu.unq.lom.histoq.backend.service.email.exception.EmailSenderException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
public class EmailSenderService implements EmailService {

    private Thread workingThread;
    private final JavaMailSender emailSender;

    EmailSenderService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendSimpleMessage(String to, String cc, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            if( cc!=null && !cc.trim().isEmpty() ) message.setCc(cc);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
        }
        catch(Exception e) {
            throw new EmailSenderException("email-sender-exception", new String[]{to,cc,e.getMessage()});
        }
    }

    @Override
    public void sendSimpleMessageAsync( String to, String cc, String subject, String text ) {
        this.workingThread = new Thread() {
            public void run() {
                sendSimpleMessage( to, cc, subject, text );
            }
        };
        this.workingThread.start();
    }
}
