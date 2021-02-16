package ar.edu.unq.lom.histoq.backend.service.internationalization;

public interface InternationalizationMessageService {

    public String getMessage( String messageId, String[] parameters );

    public String getMessage( String messageId );

}
