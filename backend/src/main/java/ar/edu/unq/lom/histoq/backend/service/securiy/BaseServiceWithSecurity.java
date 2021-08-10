package ar.edu.unq.lom.histoq.backend.service.securiy;

import ar.edu.unq.lom.histoq.backend.model.user.User;
import ar.edu.unq.lom.histoq.backend.service.BaseService;
import ar.edu.unq.lom.histoq.backend.service.context.HistoQAppContext;
import ar.edu.unq.lom.histoq.backend.service.internationalization.InternationalizationMessageService;
import ar.edu.unq.lom.histoq.backend.service.internationalization.MessageService;

public class BaseServiceWithSecurity extends BaseService {
    private final SecurityService securityService;

    protected BaseServiceWithSecurity(SecurityService securityService) {
        this.securityService = securityService;
    }

    protected User getLoggedUser() {
        return this.securityService.getLoggedUser();
    }

    protected void accessControl(SecurityService.AccessLevel level, User transactionUser) {
        this.securityService.accessControl(level,transactionUser);
    }

    protected void userAccessControl(User transactionUser) {
        this.securityService.userAccessControl(transactionUser);
    }

    protected void adminAccessControl() {
        this.securityService.adminAccessControl();
    }

    protected String getMessage(String messageId) {
        return getMessage(messageId, null);
    }

    protected String getMessage(String messageId, String[] parameters) {
        if( messageId == null ) return null;

        InternationalizationMessageService service =
                HistoQAppContext.getBean(MessageService.class);

        return parameters != null ?
                service.getMessage(messageId, parameters) :
                service.getMessage(messageId) ;
    }
}
