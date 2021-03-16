package ar.edu.unq.lom.histoq.backend.controller.security.token;

import ar.edu.unq.lom.histoq.backend.service.config.ApplicationConfigProperties;
import ar.edu.unq.lom.histoq.backend.service.context.HistoQAppContext;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.util.Collections;

public class GoogleTokenValidator implements TokenValidator {

    public static String getProviderName() {
        return "GOOGLE";
    }

    @Override
    public String validateToken(String token) throws Exception {
        String socialId = null;
        ApplicationConfigProperties appConfig = HistoQAppContext.getBean(ApplicationConfigProperties.class);
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),new JacksonFactory())
                .setAudience(Collections.singletonList(appConfig.getGoogleClientId()))
                .build();
        GoogleIdToken idToken = verifier.verify(token);

        if( idToken != null ) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            if(Boolean.valueOf(payload.getEmailVerified()))
                socialId = payload.getEmail();
        }

        return socialId;
    }
}
