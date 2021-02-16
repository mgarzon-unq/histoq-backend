package ar.edu.unq.lom.histoq.backend.repository.protocol.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class ProtocolNotFoundException extends InternationalizationException {
    public ProtocolNotFoundException(String messageId) {super(messageId);}
    public ProtocolNotFoundException(String messageId, String[] parameters) {super(messageId,parameters);}
}
