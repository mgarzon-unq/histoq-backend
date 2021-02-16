package ar.edu.unq.lom.histoq.backend.repository.image.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class ImageBatchNotFoundException extends InternationalizationException {
    public ImageBatchNotFoundException(String messageId) {super(messageId);}
    public ImageBatchNotFoundException(String messageId, String[] parameters) {super(messageId,parameters);}
}
