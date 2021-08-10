package ar.edu.unq.lom.histoq.backend.controller.advice;

import ar.edu.unq.lom.histoq.backend.repository.processJob.exception.ProcessJobNotFoundException;
import ar.edu.unq.lom.histoq.backend.repository.user.exception.UnauthorizedAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ProcessJobControllerAdvice {

    @ResponseBody
    @ExceptionHandler(ProcessJobNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String processJobNotFoundException(ProcessJobNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String unauthorizedAccessException(UnauthorizedAccessException ex) {
        return ex.getMessage();
    }
}
