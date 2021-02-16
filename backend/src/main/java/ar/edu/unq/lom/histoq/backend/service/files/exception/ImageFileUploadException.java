package ar.edu.unq.lom.histoq.backend.service.files.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class ImageFileUploadException extends InternationalizationException {
    public ImageFileUploadException(String messageId) {super(messageId);}
    public ImageFileUploadException(String messageId, String[] parameters) {super(messageId,parameters);}
}
