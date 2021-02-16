package ar.edu.unq.lom.histoq.backend.model.image.algorithms;

import java.util.Map;

public interface TissueAnalysisAlgorithm {
    public TissueData findTissueAreas(String inputFilePath, String outputFilePath);

    Map<String,String> getDefaultProcessingParameters();
    public void resetProcessingParametersToDefault();
    public void setProcessingParameterValue(String name, String value);

}
