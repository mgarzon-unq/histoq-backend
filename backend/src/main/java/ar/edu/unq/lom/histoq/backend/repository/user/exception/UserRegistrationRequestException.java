package ar.edu.unq.lom.histoq.backend.repository.user.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class UserRegistrationRequestException extends InternationalizationException {
    public UserRegistrationRequestException(String messageId) {super(messageId);}
    public UserRegistrationRequestException(String messageId, String[] parameters) {super(messageId,parameters);}
}