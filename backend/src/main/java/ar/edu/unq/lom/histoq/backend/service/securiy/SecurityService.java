package ar.edu.unq.lom.histoq.backend.service.securiy;

import ar.edu.unq.lom.histoq.backend.repository.user.exception.UnauthorizedAccessException;
import ar.edu.unq.lom.histoq.backend.model.user.User;
import ar.edu.unq.lom.histoq.backend.service.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    private UserService userService;
    private User loggedUser;

    public enum AccessLevel {
      User,
      Administrator;
    };

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public User setLoggedUser(String socialId) {
        this.loggedUser = this.userService.findUserBySocialId(socialId, false);
        return this.loggedUser;
    }

    public User getLoggedUser() {
        return this.loggedUser;
    }

    public void accessControl(AccessLevel level, User transactionUser) {
        if( level == AccessLevel.Administrator ) adminAccessControl();
        else userAccessControl(transactionUser);
    }

    public void userAccessControl(User transactionUser) {
        if( getLoggedUser()==null || !getLoggedUser().isActive() ||
            (transactionUser!=null && transactionUser.getId()!=getLoggedUser().getId()) )
            raiseUnauthorizedAccessException();
    }

    public void adminAccessControl() {
        userAccessControl(null);
        if( !getLoggedUser().isAdmin() )
            raiseUnauthorizedAccessException();
    }

    private void raiseUnauthorizedAccessException() {
        throw new UnauthorizedAccessException("unauthorized-access-exception");
    }

}
