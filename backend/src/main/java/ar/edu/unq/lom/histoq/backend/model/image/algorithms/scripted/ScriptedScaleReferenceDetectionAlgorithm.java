package ar.edu.unq.lom.histoq.backend.model.image.algorithms.scripted;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.exception.AlgorithmException;
import ar.edu.unq.lom.histoq.backend.service.config.algorithms.scripted.ScriptedScaleReferenceDetectionAlgorithmConfig;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.ImageScaleReference;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.ScaleReferenceDetectionAlgorithm;
//import org.python.core.PyString;
//import org.python.core.PyTuple;

public class ScriptedScaleReferenceDetectionAlgorithm extends ScriptedBaseAlgorithm<ScriptedScaleReferenceDetectionAlgorithmConfig>
                                                        implements ScaleReferenceDetectionAlgorithm {

    public ScriptedScaleReferenceDetectionAlgorithm(){super(ScriptedScaleReferenceDetectionAlgorithmConfig.class);}

    @Override
    public ImageScaleReference findScaleReference(String filePath) {
        try {
            loadFunction();

            /*PyTuple resultTuple = (PyTuple) getFunction().__call__(new PyString(filePath));

            return new ImageScaleReference(getStringFromTuple(resultTuple,getConfig().getMeasurementUnitResultTupleIndex()),
                    getIntegerFromTuple(resultTuple,getConfig().getScaleValueResultTupleIndex()),
                    getIntegerFromTuple(resultTuple,getConfig().getScalePixelsResultTupleIndex()));*/
            return new ImageScaleReference();
        }
        catch( Exception e ) {
            throw new AlgorithmException("scripted.scale.reference.detection.algorithm-exception",
                    new String[]{ getConfig().getFunctionName(), e.getMessage() }
                    );
        }
    }
}
