package ar.edu.unq.lom.histoq.backend.common;

import ar.edu.unq.lom.histoq.backend.service.context.HistoQAppContext;
import ar.edu.unq.lom.histoq.backend.service.internationalization.InternationalizationMessageService;
import ar.edu.unq.lom.histoq.backend.service.internationalization.MessageService;
import lombok.Data;

@Data
public class InternationalizationException extends RuntimeException {
    private String messageId;
    private String[] parameters;

    public InternationalizationException(String messageId) {this(messageId,null);}

    public InternationalizationException(String messageId, String[] parameters) {
        super(messageId);
        setMessageId(messageId);
        setParameters(parameters);
    }

    @Override
    public String getMessage() {
        if( getMessageId() == null ) return null;

        InternationalizationMessageService service =
                HistoQAppContext.getBean(MessageService.class);

        return getParameters() != null ?
                service.getMessage(getMessageId(),getParameters()) :
                service.getMessage(getMessageId()) ;
    }
}
