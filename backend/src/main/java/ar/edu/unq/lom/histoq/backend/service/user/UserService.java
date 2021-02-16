package ar.edu.unq.lom.histoq.backend.service.user;

import ar.edu.unq.lom.histoq.backend.repository.user.UserRegistrationRequestRepository;
import ar.edu.unq.lom.histoq.backend.repository.user.UserRepository;
import ar.edu.unq.lom.histoq.backend.repository.user.exception.UserAlreadyExistsException;
import ar.edu.unq.lom.histoq.backend.repository.user.exception.UserNotFoundException;
import ar.edu.unq.lom.histoq.backend.repository.user.exception.UserRegistrationRequestNotFoundException;
import ar.edu.unq.lom.histoq.backend.service.email.EmailService;
import ar.edu.unq.lom.histoq.backend.service.internationalization.InternationalizationMessageService;
import ar.edu.unq.lom.histoq.backend.service.securiy.BaseServiceWithSecurity;
import ar.edu.unq.lom.histoq.backend.service.securiy.SecurityService;
import ar.edu.unq.lom.histoq.backend.model.user.User;
import ar.edu.unq.lom.histoq.backend.model.user.UserRegistrationRequest;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService extends BaseServiceWithSecurity {
    private final UserRepository userRepository;
    private final UserRegistrationRequestRepository userRegistrationRequestRepository;
    private final EmailService emailService;
    private final InternationalizationMessageService internationalizationMessageService;

    UserService(UserRepository userRepository,
                UserRegistrationRequestRepository userRegistrationRequestRepository,
                EmailService emailService,
                InternationalizationMessageService internationalizationMessageService,
                SecurityService securityService) {
        super(securityService);
        securityService.setUserService(this);
        this.userRepository = userRepository;
        this.userRegistrationRequestRepository = userRegistrationRequestRepository;
        this.emailService = emailService;
        this.internationalizationMessageService = internationalizationMessageService;
    }

    public User findUserBySocialId(String socialId, boolean controlActiveUser) {
        if( controlActiveUser ) userAccessControl(null);

        return this.userRepository
                .findByEmail(socialId)
                .orElseThrow(() -> new UserNotFoundException("repository.user-social-id-not-found",
                        new String[]{socialId})
                );
    }

    @Transactional
    public UserRegistrationRequest createUserRegistrationRequest(UserRegistrationRequest request) {
        return this.userRegistrationRequestRepository.save(request);
    }

    @Transactional
    public UserRegistrationRequest changeUserRegistrationRequest(UserRegistrationRequest request) {
        adminAccessControl();
        return this.userRegistrationRequestRepository.save(request);
    }

    public List<UserRegistrationRequest> findPendingUserRegistrationRequests() {
        adminAccessControl();
        return  this.userRegistrationRequestRepository
                .findByProcessed(false)
                .orElse(new ArrayList<>());
    }

    public List<User> findAllUsers() {
        adminAccessControl();
        return this.userRepository.findAll();
    }

    @Transactional
    public User createUser(User user) {
        adminAccessControl();
        checkIfUserAlreadyExists(user);
        user = this.userRepository.save(user);
        notifyAccessGranted(user);
        return user;
    }

    @Transactional
    public User changeUser(User user) {
        adminAccessControl();
        return this.userRepository.save(user);
    }

    @Transactional
    public User acceptUserRegistrationRequest(UserRegistrationRequest userRegistrationRequest) {
        userRegistrationRequest = findUserRegistrationRequestById(userRegistrationRequest.getId());

        User user = createUser( new User(userRegistrationRequest.getFirstName(),
                userRegistrationRequest.getLastName(),
                userRegistrationRequest.getSocialId(),
                false));

        userRegistrationRequest.setProcessed(true);
        changeUserRegistrationRequest(userRegistrationRequest);

        return user;
    }

    @Transactional
    public UserRegistrationRequest rejectUserRegistrationRequest(Long requestId) {
        UserRegistrationRequest request = findUserRegistrationRequestById(requestId);

        request.setProcessed(true);

        return changeUserRegistrationRequest(request);
    }


    public UserRegistrationRequest findUserRegistrationRequestById(Long requestId) {
        adminAccessControl();
        return this.userRegistrationRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new UserRegistrationRequestNotFoundException("repository.user-registration-request-not-found",
                        new String[]{requestId.toString()}));
    }

    private void notifyAccessGranted(User user) {
        this.emailService.sendSimpleMessageAsync(user.getEmail(), null,
                this.internationalizationMessageService.getMessage("email-text.user-registration-request-accepted-subject"),
                this.internationalizationMessageService.getMessage("email-text.user-registration-request-accepted-body",
                        new String[]{user.getFirstName()}));
    }

    private void checkIfUserAlreadyExists(User user) {
        if( this.userRepository.findByEmail(user.getEmail()).isPresent() )
            throw new UserAlreadyExistsException("repository.user-user-already-exists", new String[]{user.getEmail()});
    }

}
