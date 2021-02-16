package ar.edu.unq.lom.histoq.backend.service.files.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class ImageFileDownloadException extends InternationalizationException {
    public ImageFileDownloadException(String messageId) {super(messageId);}
    public ImageFileDownloadException(String messageId, String[] parameters) {super(messageId,parameters);}
}
