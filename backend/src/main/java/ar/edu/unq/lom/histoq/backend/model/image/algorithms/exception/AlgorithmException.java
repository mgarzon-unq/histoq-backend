package ar.edu.unq.lom.histoq.backend.model.image.algorithms.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class AlgorithmException extends InternationalizationException {
    public AlgorithmException(String messageId) {super(messageId);}
    public AlgorithmException(String messageId, String[] parameters) {super(messageId,parameters);}
}
