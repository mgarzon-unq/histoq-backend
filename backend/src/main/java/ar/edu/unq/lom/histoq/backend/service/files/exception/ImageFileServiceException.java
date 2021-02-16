package ar.edu.unq.lom.histoq.backend.service.files.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class ImageFileServiceException extends InternationalizationException {
    public ImageFileServiceException(String messageId) {super(messageId);}
    public ImageFileServiceException(String messageId, String[] parameters) {super(messageId,parameters);}
}
