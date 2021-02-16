package ar.edu.unq.lom.histoq.backend.service.config;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.ScaleReferenceDetectionAlgorithm;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.StitchingAlgorithm;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.TissueAnalysisAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Component
@Validated
@ConfigurationProperties(prefix = "histoq")
@Data
public class ApplicationConfigProperties {

    @NotNull
    StitchingAlgorithm stitchingAlgorithm;

    @NotNull
    ScaleReferenceDetectionAlgorithm scaleReferenceDetectionAlgorithm;

    @NotNull
    TissueAnalysisAlgorithm tissueAnalysisAlgorithm;

    @NotEmpty
    private String  rootFolder;

    @NotEmpty
    private String  scannedImagePrefix;

    @NotEmpty
    private String  stitchedImagePrefix;

    @NotEmpty
    private String  googleClientId;

    @NotEmpty
    private String  facebookClientId;

    @NotEmpty
    private String  googleReCaptchaEndpoint;

    @NotEmpty
    private String googleReCaptchaSiteKey;

    @NotEmpty
    private String googleReCaptchaSecretKey;

    @NotNull
    @Min(0)
    @Max(1)
    private Double googleReCaptchaV3Threshold;

    @NotEmpty
    private String corsAllowedOrigins;

}
