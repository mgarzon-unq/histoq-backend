package ar.edu.unq.lom.histoq.backend.service.config.algorithms.scripted;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Component
@Validated
@ConfigurationProperties(prefix = "scripted.scale.reference.detection.algorithm")
@PropertySource("classpath:scripted_scale_reference_detection_algorithm.properties")
@Data
public class ScriptedScaleReferenceDetectionAlgorithmConfig implements ScriptedAlgorithmConfig {
    @NotEmpty
    private String  functionImportStatement;

    @NotEmpty
    private String  functionName;

    @NotNull
    @Min(0)
    private Integer  measurementUnitResultTupleIndex;

    @NotNull
    @Min(0)
    private Integer  scaleValueResultTupleIndex;

    @NotNull
    @Min(0)
    private Integer  scalePixelsResultTupleIndex;
}
