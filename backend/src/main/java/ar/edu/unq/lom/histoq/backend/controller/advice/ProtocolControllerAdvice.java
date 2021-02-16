package ar.edu.unq.lom.histoq.backend.controller.advice;

import ar.edu.unq.lom.histoq.backend.repository.protocol.exception.IndividualNotFoundException;
import ar.edu.unq.lom.histoq.backend.repository.protocol.exception.ProtocolNotFoundException;
import ar.edu.unq.lom.histoq.backend.repository.user.exception.UnauthorizedAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ProtocolControllerAdvice {

    @ResponseBody
    @ExceptionHandler(ProtocolNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String protocolNotFoundHandler(ProtocolNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(IndividualNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String protocolNotFoundHandler(IndividualNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String dataExportException(UnauthorizedAccessException ex) {
        return ex.getMessage();
    }

}
