package ar.edu.unq.lom.histoq.backend.service.config;

import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationPropertiesBinding
public class SizeConverter implements Converter<String, Size> {

    @Override
    public Size convert(String from) {
        Size size = null;
        if( !StringUtils.isEmpty(from)  ) {
            String tokens[] = from.replace("(","").replace(")","").split(",");
            size = new Size( Double.parseDouble(tokens[0]),
                    Double.parseDouble(tokens[1]));
        }
        return size;
    }

}