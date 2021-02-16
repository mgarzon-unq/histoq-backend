package ar.edu.unq.lom.histoq.backend.service.securiy;

import ar.edu.unq.lom.histoq.backend.model.user.User;

public class BaseServiceWithSecurity {
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
}
