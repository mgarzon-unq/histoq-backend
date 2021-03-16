package ar.edu.unq.lom.histoq.backend.controller.advice;

import ar.edu.unq.lom.histoq.backend.repository.image.exception.ImageBatchNotFoundException;
import ar.edu.unq.lom.histoq.backend.repository.image.exception.ImageFileNotFoundException;
import ar.edu.unq.lom.histoq.backend.repository.image.exception.ImageNotFoundException;
import ar.edu.unq.lom.histoq.backend.repository.user.exception.UnauthorizedAccessException;
import ar.edu.unq.lom.histoq.backend.model.image.exception.ImageScannerException;
import ar.edu.unq.lom.histoq.backend.service.dataexport.exception.DataExportException;
import ar.edu.unq.lom.histoq.backend.service.files.exception.ImageFileDownloadException;
import ar.edu.unq.lom.histoq.backend.service.files.exception.ImageFileUploadException;
import ar.edu.unq.lom.histoq.backend.service.internationalization.InternationalizationMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;

@ControllerAdvice
public class ImageControllerAdvice {


    private InternationalizationMessageService messageService;

    ImageControllerAdvice(InternationalizationMessageService messageService) {
        this.messageService = messageService;
    }

    @ResponseBody
    @ExceptionHandler(ImageFileUploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String imageFileUploadException(ImageFileUploadException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ImageFileDownloadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String imageFileDownloadException(ImageFileDownloadException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String imageFileDownloadException(IOException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ImageFileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String imageFileNotFoundException(ImageFileNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ImageBatchNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String imageBatchNotFoundException(ImageBatchNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ImageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String imageNotFoundException(ImageNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ImageScannerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String imageNotFoundException(ImageScannerException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return messageService.getMessage("image.file-too-large-error",
                new String[]{exc.getMessage()});
    }

    @ResponseBody
    @ExceptionHandler(DataExportException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String dataExportException(DataExportException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String dataExportException(UnauthorizedAccessException ex) {
        return ex.getMessage();
    }

}
