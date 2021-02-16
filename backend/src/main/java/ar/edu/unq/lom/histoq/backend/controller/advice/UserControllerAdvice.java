package ar.edu.unq.lom.histoq.backend.controller.advice;

import ar.edu.unq.lom.histoq.backend.controller.security.excepion.ReCaptchaValidationException;
import ar.edu.unq.lom.histoq.backend.repository.user.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UserControllerAdvice {

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String userNotFoundException(UserNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UserRegistrationRequestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String userRegistrationRequestNotFoundException(UserRegistrationRequestNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UserRegistrationRequestException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String userRegistrationRequestException(UserRegistrationRequestException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String unauthorizedAccessException(UnauthorizedAccessException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ReCaptchaValidationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String reCaptchaValidationException(ReCaptchaValidationException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public String userAlreadyExistsException(UserAlreadyExistsException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String generalException(Exception ex) {
        return ex.getMessage();
    }

}
