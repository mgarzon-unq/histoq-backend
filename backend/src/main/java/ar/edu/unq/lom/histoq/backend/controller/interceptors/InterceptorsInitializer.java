package ar.edu.unq.lom.histoq.backend.controller.interceptors;

import ar.edu.unq.lom.histoq.backend.controller.security.AccessValidationInterceptor;
import ar.edu.unq.lom.histoq.backend.service.config.ApplicationConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class InterceptorsInitializer implements WebMvcConfigurer {

    private final ApplicationConfigProperties applicationConfigProperties;
    private final AccessValidationInterceptor accessValidationInterceptor;

    InterceptorsInitializer(ApplicationConfigProperties applicationConfigProperties,
                            AccessValidationInterceptor accessValidationInterceptor) {
        this.applicationConfigProperties = applicationConfigProperties;
        this.accessValidationInterceptor = accessValidationInterceptor;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(this.accessValidationInterceptor).addPathPatterns("/**");
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(this.applicationConfigProperties.getCorsAllowedOrigins().split(","))
                ;
    }
}
