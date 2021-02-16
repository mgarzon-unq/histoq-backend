package ar.edu.unq.lom.histoq.backend.service.config.algorithms.scripted;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotEmpty;

@Component
@Validated
@ConfigurationProperties(prefix = "scripted.stitching.algorithm")
@PropertySource("classpath:scripted_stitching_algorithm.properties")
@Data
public class ScriptedStitchingAlgorithmConfig implements ScriptedAlgorithmConfig {
    @NotEmpty
    private String  functionImportStatement;

    @NotEmpty
    private String  functionName;
}
