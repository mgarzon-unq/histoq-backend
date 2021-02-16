package ar.edu.unq.lom.histoq.backend.controller.security.token;

import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;

public class FacebookTokenValidator implements TokenValidator {

    public static String getProviderName() {
        return "FACEBOOK";
    }

    @Override
    public String validateToken(String token) throws Exception {
        String socialId = null;
        Facebook facebook = new FacebookTemplate(token);
        User user = facebook.fetchObject("me",User.class,"email");
        if( user != null)
            socialId = user.getEmail();
        return socialId;
    }
}
