package ar.edu.unq.lom.histoq.backend.repository.processJob.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class ProcessJobNotFoundException extends InternationalizationException {
    public ProcessJobNotFoundException(String messageId) {super(messageId);}
    public ProcessJobNotFoundException(String messageId, String[] parameters) {super(messageId,parameters);}
}

