package ar.edu.unq.lom.histoq.backend.service.config.algorithms;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.AlgorithmFactory;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.StitchingAlgorithm;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class StitchingAlgorithmConverter implements Converter<String, StitchingAlgorithm> {
    @Override
    public StitchingAlgorithm convert(String from) {
        return AlgorithmFactory.getStitchingAlgorithm(from);
    }
}
