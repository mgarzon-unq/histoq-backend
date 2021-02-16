package ar.edu.unq.lom.histoq.backend.controller.security.excepion;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;

public class ReCaptchaValidationException extends InternationalizationException {
    public ReCaptchaValidationException(String messageId) {super(messageId);}
    public ReCaptchaValidationException(String messageId, String[] parameters) {super(messageId,parameters);}
}
