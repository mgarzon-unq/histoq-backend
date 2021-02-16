package ar.edu.unq.lom.histoq.backend.repository.image.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class ImageFileNotFoundException extends InternationalizationException {
    public ImageFileNotFoundException(String messageId) {super(messageId);}
    public ImageFileNotFoundException(String messageId, String[] parameters) {super(messageId,parameters);}
}

