package ar.edu.unq.lom.histoq.backend.service.config.algorithms;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.AlgorithmFactory;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.ScaleReferenceDetectionAlgorithm;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class ScaleReferenceDetectionAlgorithmConverter implements Converter<String, ScaleReferenceDetectionAlgorithm> {
    @Override
    public ScaleReferenceDetectionAlgorithm convert(String from) {
        return AlgorithmFactory.getScaleReferenceDetectionAlgorithm(from);
    }
}

