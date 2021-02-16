package ar.edu.unq.lom.histoq.backend.repository.image.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class ImageNotFoundException extends InternationalizationException {
    public ImageNotFoundException(String messageId) {super(messageId);}
    public ImageNotFoundException(String messageId, String[] parameters) {super(messageId,parameters);}
}
