package ar.edu.unq.lom.histoq.backend.repository.user.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class UnauthorizedAccessException extends InternationalizationException {
    public UnauthorizedAccessException(String messageId) {super(messageId);}
    public UnauthorizedAccessException(String messageId, String[] parameters) {super(messageId,parameters);}
}
