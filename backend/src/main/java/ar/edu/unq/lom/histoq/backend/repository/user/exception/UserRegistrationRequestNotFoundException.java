package ar.edu.unq.lom.histoq.backend.repository.user.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class UserRegistrationRequestNotFoundException extends InternationalizationException {
    public UserRegistrationRequestNotFoundException(String messageId) {super(messageId);}
    public UserRegistrationRequestNotFoundException(String messageId, String[] parameters) {super(messageId,parameters);}
}