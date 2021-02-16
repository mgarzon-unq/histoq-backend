package ar.edu.unq.lom.histoq.backend.model.image.algorithms;

import ar.edu.unq.lom.histoq.backend.service.context.TissueScanAppContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseAlgorithm<T> {
    private T configProperties;
    private Class configPropertiesClass;
    private Map<String,String> customParameters = new HashMap();
    private ObjectMapper mapper = new ObjectMapper();

    protected BaseAlgorithm(Class configPropertiesClass) {
        this.configPropertiesClass = configPropertiesClass;
    }

    protected T getConfig() {
        if( this.configProperties == null )
            this.configProperties = (T) TissueScanAppContext.getBean(this.configPropertiesClass);
        return this.configProperties;
    }

    protected void resetProcessingParametersToDefault() {
       this.customParameters.clear();
    }

    protected void setProcessingParameterValue(String name, String value) {
        this.customParameters.put(name,value);
    }

    protected Object getProcessingParameterValue(String name, Class classReference) {
        try {
            String value = this.customParameters.get(name);

            if( value != null )
                return this.mapper.readValue(value, classReference);
            else
                return this.configProperties.getClass()
                        .getMethod("get" + name)
                        .invoke(this.configProperties);
        }
        catch(Exception e) {
            return null;
        }
    }

    protected String getValueAsJson(Object value) {
        try {
            return this.mapper.writeValueAsString(value);
        }
        catch(Exception ex) {
            return null;
        }
    }

    protected void addJsonDeserializer(Class classReference, StdDeserializer deserializer) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(classReference, deserializer);
        this.mapper.registerModule(module);
    }

    protected void addJsonSerializer(Class classReference, StdSerializer serializer) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(classReference, serializer);
        this.mapper.registerModule(module);
    }

}
