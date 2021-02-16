package ar.edu.unq.lom.histoq.backend.repository.user.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class UserAlreadyExistsException extends InternationalizationException {
    public UserAlreadyExistsException(String messageId) {super(messageId);}
    public UserAlreadyExistsException(String messageId, String[] parameters) {super(messageId,parameters);}
}
