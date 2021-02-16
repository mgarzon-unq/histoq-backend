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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Component
@Validated
@ConfigurationProperties(prefix = "scale.reference.detection.algorithm")
@PropertySource("classpath:embedded_scale_reference_detection_algorithm.properties")
@Data
public class EmbeddedScaleReferenceDetectionAlgorithmConfig {

    @NotNull
    @Min(0)
    private Integer straightLineTolerance;

    @Min(0)
    @Max(255)
    private Integer referenceTextThresholdValue;

    @Min(0)
    @Max(255)
    private Integer referenceTextThresholdMaxValue;

    @NotEmpty
    private String  referenceTextMatchRegex;

    @NotEmpty
    private String  referenceTextSplitRegex;

    @NotNull
    private Scalar referenceTextContourColor;

    @NotNull
    private Size referenceLineGaussianBlurKernelSize;

    @Min(0)
    @Max(255)
    private Integer referenceLineCannyThreshold1;

    @Min(0)
    @Max(255)
    private Integer referenceLineCannyThreshold2;

    @NotNull
    private Double  referenceLineHoughlinespRho;

    @NotNull
    private Double  referenceLineHoughlinespTheta;

    @Min(0)
    @Max(255)
    private Integer referenceLineHoughlinespThreshold;

    @NotNull
    @Min(1)
    private Double  referenceLineHoughlinespMinLineLength;

    @NotNull
    @Min(1)
    private Double  referenceLineHoughlinespMaxLineGap;

    @NotNull
    @Min(0)
    private Integer referenceLinePixelsToAdd;

    @NotNull
    private Scalar  referenceLineContourColor;

    @NotEmpty
    private String tesseractDataPath;

    @NotEmpty
    private String tesseractLanguage;

    @NotNull
    @Min(0)
    @Max(13)
    private Integer tesseractPageSegmentationMode;

    @NotNull
    @Min(0)
    @Max(3)
    private Integer tesseractOcrEngineMode;

}
