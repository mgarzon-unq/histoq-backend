package ar.edu.unq.lom.histoq.backend.service.files.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class UnknownFileStorageServiceTypeException extends InternationalizationException {
    public UnknownFileStorageServiceTypeException(String messageId) {super(messageId);}
    public UnknownFileStorageServiceTypeException(String messageId, String[] parameters) {super(messageId,parameters);}
}
