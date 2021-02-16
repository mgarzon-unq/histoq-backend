package ar.edu.unq.lom.histoq.backend.service.email.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class EmailSenderException extends InternationalizationException {
    public EmailSenderException(String messageId) {super(messageId);}
    public EmailSenderException(String messageId, String[] parameters) {super(messageId,parameters);}
}
