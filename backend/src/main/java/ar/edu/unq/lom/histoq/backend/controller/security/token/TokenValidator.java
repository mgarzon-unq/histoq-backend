package ar.edu.unq.lom.histoq.backend.controller.security.token;

public interface TokenValidator {
    String validateToken(String token) throws Exception;
}
