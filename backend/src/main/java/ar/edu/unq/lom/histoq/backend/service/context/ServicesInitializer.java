package ar.edu.unq.lom.histoq.backend.service.context;

import ar.edu.unq.lom.histoq.backend.service.internationalization.InternationalizationMessageService;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;


@Configuration
@Slf4j
public class ServicesInitializer {

    private final InternationalizationMessageService messageService;
    ServicesInitializer(InternationalizationMessageService messageService) {
        this.messageService = messageService;
    }

    @Bean
    CommandLineRunner initOpenCV() {
        return args -> {
            log.info(messageService.getMessage("log.opencv.init"));
            Loader.load(opencv_java.class);
        };
    }

}
