package ar.edu.unq.lom.histoq.backend.model.image.algorithms.scripted;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.exception.AlgorithmException;
import ar.edu.unq.lom.histoq.backend.service.config.algorithms.scripted.ScriptedStitchingAlgorithmConfig;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.StitchingAlgorithm;
//import org.python.core.PyList;
//import org.python.core.PyString;
import java.util.List;

public class ScriptedStitchingAlgorithm extends ScriptedBaseAlgorithm<ScriptedStitchingAlgorithmConfig>
                                        implements StitchingAlgorithm {

    public ScriptedStitchingAlgorithm() {
        super(ScriptedStitchingAlgorithmConfig.class);
    }

    @Override
    public void stitchImageFiles(List<String> filePaths, String outputFilePath) throws AlgorithmException {
        try {
            loadFunction();

            //getFunction().__call__(new PyList(filePaths),new PyString(outputFilePath));
        }
        catch( Exception e ) {
            throw new AlgorithmException("scripted.stitching.algorithm-exception",
                    new String[]{ getConfig().getFunctionName(), e.getMessage() }
                    );
        }
    }

}
