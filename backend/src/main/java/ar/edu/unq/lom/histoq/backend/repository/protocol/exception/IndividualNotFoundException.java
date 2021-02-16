package ar.edu.unq.lom.histoq.backend.repository.protocol.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class IndividualNotFoundException extends InternationalizationException {
    public IndividualNotFoundException(String messageId) {super(messageId);}
    public IndividualNotFoundException(String messageId, String[] parameters) {super(messageId,parameters);}

}