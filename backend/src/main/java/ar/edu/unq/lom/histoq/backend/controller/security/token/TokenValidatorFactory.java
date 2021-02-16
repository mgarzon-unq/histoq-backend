package ar.edu.unq.lom.histoq.backend.controller.security.token;

import java.util.HashMap;
import java.util.Map;

public class TokenValidatorFactory {

    static private Map<String, TokenValidator> supportedValidators = new HashMap<String, TokenValidator>() {{
        put(GoogleTokenValidator.getProviderName(), new GoogleTokenValidator());
        put(FacebookTokenValidator.getProviderName(), new FacebookTokenValidator());
    }};

    static public TokenValidator getTokenValidator(String providerName) {
        TokenValidator validator = TokenValidatorFactory.supportedValidators.get(providerName);
        if( validator == null )
            throw new IllegalArgumentException("Unknown Authentication Provider: " + providerName);
        return validator;
    }
}
