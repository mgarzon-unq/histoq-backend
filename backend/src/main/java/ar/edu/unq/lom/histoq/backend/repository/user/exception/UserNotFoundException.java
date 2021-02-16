package ar.edu.unq.lom.histoq.backend.repository.user.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class UserNotFoundException extends InternationalizationException {
    public UserNotFoundException(String messageId) {super(messageId);}
    public UserNotFoundException(String messageId, String[] parameters) {super(messageId,parameters);}
}
