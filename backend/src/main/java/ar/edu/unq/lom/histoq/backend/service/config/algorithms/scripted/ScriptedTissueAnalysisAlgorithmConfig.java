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
@ConfigurationProperties(prefix = "scripted.tissue.analysis.algorithm")
@PropertySource("classpath:scripted_tissue_analysis_algorithm.properties")
@Data
public class ScriptedTissueAnalysisAlgorithmConfig implements ScriptedAlgorithmConfig {
    @NotEmpty
    private String  functionImportStatement;

    @NotEmpty
    private String  functionName;

    @NotNull
    @Min(0)
    private Integer  totalAreaResultTupleIndex;

    @NotNull
    @Min(0)
    private Integer  totalTissueAreaResultTupleIndex;

    @NotNull
    @Min(0)
    private Integer  viableTissueAreaResultTupleIndex;

    @NotNull
    @Min(0)
    private Integer  necroticTissueAreaResultTupleIndex;
}
