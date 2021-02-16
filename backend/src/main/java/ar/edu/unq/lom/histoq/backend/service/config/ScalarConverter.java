package ar.edu.unq.lom.histoq.backend.service.config;

import org.opencv.core.Scalar;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationPropertiesBinding
public class ScalarConverter implements Converter<String, Scalar> {

    @Override
    public Scalar convert(String from) {
        Scalar scalar = null;
        if( !StringUtils.isEmpty(from)  ) {
            String tokens[] = from.replace("(","").replace(")","").split(",");
            scalar = new Scalar( Double.parseDouble(tokens[0]),
                                    Double.parseDouble(tokens[1]),
                                        Double.parseDouble(tokens[2]));
        }
        return scalar;
    }
}
