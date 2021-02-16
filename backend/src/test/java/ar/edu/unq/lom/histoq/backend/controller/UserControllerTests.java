package ar.edu.unq.lom.histoq.backend.controller;

import ar.edu.unq.lom.histoq.backend.model.user.User;
import ar.edu.unq.lom.histoq.backend.model.user.UserRegistrationRequest;
import ar.edu.unq.lom.histoq.backend.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserControllerTests {
    private UserController userController;

    @Mock
    private UserService userService;


    @BeforeEach
    public void setUp() {
        this.userController = new UserController(this.userService);
    }

    @Test
    void findUserBySocialIdTest() {
        String email = "";

        this.userController.findUserBySocialId(email);

        verify(this.userService,times(1)).findUserBySocialId(email,true);
    }

    @Test
    void findAllUsersTest() {
        this.userController.findAllUsers();

        verify(this.userService,times(1)).findAllUsers();
    }

    @Test
    void createUserTest() {
        User user = mock(User.class);

        this.userController.createUser(user);

        verify(this.userService,times(1)).createUser(user);
    }

    @Test
    void changeUserTest() {
        User user = mock(User.class);

        this.userController.changeUser(user);

        verify(this.userService,times(1)).changeUser(user);
    }

    @Test
    void createUserRegistrationRequestTest() {
        UserRegistrationRequest request = mock(UserRegistrationRequest.class);

        this.userController.createUserRegistrationRequest(request);

        verify(this.userService,times(1)).createUserRegistrationRequest(request);
    }

    @Test
    void acceptUserRegistrationRequestTest() {
        UserRegistrationRequest request = mock(UserRegistrationRequest.class);

        this.userController.acceptUserRegistrationRequest(request);

        verify(this.userService,times(1)).acceptUserRegistrationRequest(request);
    }

    @Test
    void rejectUserRegistrationRequestTest() {
        Long requestId = 1L;

        this.userController.rejectUserRegistrationRequest(requestId);

        verify(this.userService,times(1)).rejectUserRegistrationRequest(requestId);
    }

    @Test
    void findPendingUserRegistrationRequestsTest() {
        this.userController.findPendingUserRegistrationRequests();

        verify(this.userService,times(1)).findPendingUserRegistrationRequests();
    }

}
