package ar.edu.unq.lom.histoq.backend.service.config.algorithms.embedded;

import lombok.Data;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Component
@Validated
@ConfigurationProperties(prefix = "tissue.analysis.algorithm")
@PropertySource("classpath:embedded_tissue_analysis_algorithm.properties")
@Data
public class EmbeddedTissueAnalysisAlgorithmConfig {

    @Min(0)
    @Max(3)
    private Integer drawContourThickness;

    @Min(0)
    @Max(1)
    private Double overlayTransparency;

    @NotNull
    private Scalar  tissueAreaContourColor;

    @NotNull
    private Size    tissueAreaGaussianBlurKernelSize;

    @Min(0)
    @Max(255)
    private Integer tissueAreaThresholdValue;

    @Min(0)
    @Max(255)
    private Integer tissueAreaThresholdMaxValue;

    @NotNull
    private Double  tissueAreaBackgroundFilterFactor;

    @NotNull
    private Scalar  viableTissueAreaContourColor;

    @NotNull
    private Scalar  viableTissueAreaOverlayColor;

    @NotNull
    private Double  viableTissueAreaFilterFactor;

    @NotNull
    private Scalar  viableTissueAreaMinHSVInRange;

    @NotNull
    private Scalar  viableTissueAreaMaxHSVInRange;

    @NotNull
    private Size    viableTissueAreaGaussianBlurKernelSize;

    @NotNull
    private Scalar  necroticTissueAreaContourColor;

    @NotNull
    private Scalar  necroticTissueAreaOverlayColor;

    @NotNull
    private Double  necroticTissueAreaMinDetectedToTotalFactor;

    @NotNull
    private Double  necroticTissueAreaUnenclosedChildrenFilterFactor;

}
