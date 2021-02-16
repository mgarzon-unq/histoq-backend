package ar.edu.unq.lom.histoq.backend.model.image.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class ImageScannerException extends InternationalizationException {
    public ImageScannerException(String messageId) {
        super(messageId);
    }
    public ImageScannerException(String messageId, String[] parameters) {
        super(messageId,parameters);
    }
}