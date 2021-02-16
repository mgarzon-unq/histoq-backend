package ar.edu.unq.lom.histoq.backend.controller.security;

import ar.edu.unq.lom.histoq.backend.controller.security.excepion.AccessValidationException;
import ar.edu.unq.lom.histoq.backend.controller.security.excepion.ReCaptchaValidationException;
import ar.edu.unq.lom.histoq.backend.controller.security.token.TokenValidatorFactory;
import ar.edu.unq.lom.histoq.backend.service.config.ApplicationConfigProperties;
import ar.edu.unq.lom.histoq.backend.service.securiy.SecurityService;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@Component
public class AccessValidationInterceptor implements HandlerInterceptor {
    private final String HEADER = "Authorization";
    private final SecurityService securityService;
    private final ApplicationConfigProperties configProperties;
    private final Map<String, String> publicEndPoints = new HashMap<String, String>() {{
        put("/users/registration-requests", "requestUserRegistration");
        //add more endpoints....
    }};

    AccessValidationInterceptor(SecurityService securityService,
                                ApplicationConfigProperties configProperties) {
        this.securityService = securityService;
        this.configProperties = configProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        //Ignore pre-flight requests so CORS is not interfered...
        if( request.getMethod().equalsIgnoreCase("OPTIONS") ) return true;

        boolean result = false;

        try {
            String token = request.getHeader(HEADER);
            String socialId;

            if( token != null ) {
                socialId = validateToken(token);
                if( socialId == null ) throw new AccessValidationException("access-invalid-token");

                this.securityService.setLoggedUser(socialId);
            }
            else
                validatePublicEndpointRequest(request);

            result = true;
        }
        catch ( Exception e ) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(e.getMessage());
        }

        return result;
    }

    private String validateToken(String token) throws Exception {
        String[] tokens = token.split(",");
        return TokenValidatorFactory.getTokenValidator(tokens[0]).validateToken(tokens[1]);
    }

    private void validatePublicEndpointRequest(HttpServletRequest request) {
        if( isPublicEndPoint(request.getRequestURI()) )
            validateCaptcha(request.getParameter("reCaptchaToken"),
                getReCaptchaActionForPublicEndPoint(request.getRequestURI()) );
        else
            throw new AccessValidationException("access-invalid-token");
    }

    private void validateCaptcha(String captchaResponse, String action){
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("secret", this.configProperties.getGoogleReCaptchaSecretKey());
        requestMap.add("response", captchaResponse);

        CaptchaResponse apiResponse = restTemplate.postForObject(this.configProperties.getGoogleReCaptchaEndpoint(),
                requestMap, CaptchaResponse.class);

        if( !(apiResponse!=null &&
                Boolean.TRUE.equals(apiResponse.getSuccess()) &&
                apiResponse.getAction().equals(action) &&
                apiResponse.getScore() >= configProperties.getGoogleReCaptchaV3Threshold()) )
            throw new ReCaptchaValidationException("recaptcha-validation-exception");
    }

    private boolean isPublicEndPoint(String url) {
        return this.publicEndPoints.containsKey(url);
    }

    private String getReCaptchaActionForPublicEndPoint(String url) {
        return this.publicEndPoints.get(url);
    }

}
