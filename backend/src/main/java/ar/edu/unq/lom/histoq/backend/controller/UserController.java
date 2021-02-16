package ar.edu.unq.lom.histoq.backend.controller;

import ar.edu.unq.lom.histoq.backend.model.user.User;
import ar.edu.unq.lom.histoq.backend.model.user.UserRegistrationRequest;
import ar.edu.unq.lom.histoq.backend.service.user.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value="/users/{socialId:.+}")
    User findUserBySocialId(@PathVariable String socialId) {
        return userService.findUserBySocialId(socialId, true);
    }

    @GetMapping(value="/users")
    List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping(value = "/users")
    User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping(value = "/users")
    User changeUser(@RequestBody User user) {
        return userService.changeUser(user);
    }

    @PostMapping(value = "/users/registration-requests")
    UserRegistrationRequest createUserRegistrationRequest(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        return userService.createUserRegistrationRequest(userRegistrationRequest);
    }

    @PutMapping(value = "/users/registration-requests")
    User acceptUserRegistrationRequest(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        return userService.acceptUserRegistrationRequest(userRegistrationRequest);
    }

    @DeleteMapping(value = "/users/registration-requests/{requestId}")
    UserRegistrationRequest rejectUserRegistrationRequest(@PathVariable Long requestId) {
        return userService.rejectUserRegistrationRequest(requestId);
    }

    @GetMapping(value="/users/registration-requests")
    List<UserRegistrationRequest> findPendingUserRegistrationRequests() {
        return userService.findPendingUserRegistrationRequests();
    }

}
