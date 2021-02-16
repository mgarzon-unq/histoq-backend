package ar.edu.unq.lom.histoq.backend.controller.security.excepion;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class AccessValidationException extends InternationalizationException {
    public AccessValidationException(String messageId) {super(messageId);}
    public AccessValidationException(String messageId, String[] parameters) {super(messageId,parameters);}
}
