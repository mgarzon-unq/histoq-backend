package ar.edu.unq.lom.histoq.backend.model.image.algorithms.scripted;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.exception.AlgorithmException;
import ar.edu.unq.lom.histoq.backend.service.config.algorithms.scripted.ScriptedTissueAnalysisAlgorithmConfig;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.TissueAnalysisAlgorithm;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.TissueData;
//import org.python.core.PyString;
//import org.python.core.PyTuple;

import java.util.HashMap;
import java.util.Map;

public class ScriptedTissueAnalysisAlgorithm extends ScriptedBaseAlgorithm<ScriptedTissueAnalysisAlgorithmConfig>
                                                implements TissueAnalysisAlgorithm {

    public ScriptedTissueAnalysisAlgorithm(){super(ScriptedTissueAnalysisAlgorithmConfig.class);}

    @Override
    public TissueData findTissueAreas(String inputFilePath, String outputFilePath) {
        try {
            loadFunction();

            //PyTuple resultTuple = (PyTuple) getFunction().__call__(new PyString(inputFilePath), new PyString(outputFilePath));

            return new ScriptedTissueData();/*
                    getDoubleFromTuple(resultTuple,getConfig().getTotalAreaResultTupleIndex()),
                    getDoubleFromTuple(resultTuple,getConfig().getTotalTissueAreaResultTupleIndex()),
                    getDoubleFromTuple(resultTuple,getConfig().getViableTissueAreaResultTupleIndex()),
                    getDoubleFromTuple(resultTuple,getConfig().getNecroticTissueAreaResultTupleIndex()) );*/
        }
        catch( Exception e ) {
            throw new AlgorithmException("scripted.tissue.analysis.algorithm-exception",
                    new String[]{ getConfig().getFunctionName(), e.getMessage() }
            );
        }
    }

    @Override
    public Map<String, String> getDefaultProcessingParameters() {
        return new HashMap<String,String>();
    }

    @Override
    public void resetProcessingParametersToDefault() {
        super.resetProcessingParametersToDefault();
    }

    @Override
    public void setProcessingParameterValue(String name, String value) {
        super.setProcessingParameterValue(name,value);
    }
}
