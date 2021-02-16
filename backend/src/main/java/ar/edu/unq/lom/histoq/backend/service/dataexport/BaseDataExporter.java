package ar.edu.unq.lom.histoq.backend.service.dataexport;

import ar.edu.unq.lom.histoq.backend.service.internationalization.InternationalizationMessageService;
import ar.edu.unq.lom.histoq.backend.service.internationalization.MessageService;
import ar.edu.unq.lom.histoq.backend.service.context.TissueScanAppContext;

public abstract class BaseDataExporter implements DataExporter {
    private InternationalizationMessageService messageService;

    BaseDataExporter() {
        this.messageService = TissueScanAppContext.getBean(MessageService.class);
    }

    protected String getTranslation(String messageId) {
        return this.messageService.getMessage(messageId);
    }

    protected String getTranslation(String messageId, String[] parameters) {
        return this.messageService.getMessage(messageId,parameters);
    }
}
