package ar.edu.unq.lom.histoq.backend.service.dataexport.exception;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class DataExportException extends InternationalizationException {
    public DataExportException(String messageId) {super(messageId);}
    public DataExportException(String messageId, String[] parameters) {super(messageId,parameters);}
}
