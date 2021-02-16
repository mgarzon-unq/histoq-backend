package ar.edu.unq.lom.histoq.backend.service.internationalization;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MessageService implements InternationalizationMessageService {

    private MessageSource messages;

    MessageService(@Qualifier("messageSource") MessageSource messages) {
        this.messages =messages;
    }

    @Override
    public String getMessage(String messageId, String[] parameters) {
        return messages.getMessage( messageId, parameters, LocaleContextHolder.getLocale());
    }

    @Override
    public String getMessage(String messageId) {
        return messages.getMessage( messageId, null, LocaleContextHolder.getLocale());
    }
}
