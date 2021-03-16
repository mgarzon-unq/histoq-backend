package ar.edu.unq.lom.histoq.backend.service.files.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class FileStoragesServiceException extends InternationalizationException {
    public FileStoragesServiceException(String messageId) {super(messageId);}
    public FileStoragesServiceException(String messageId, String[] parameters) {super(messageId,parameters);}
}

