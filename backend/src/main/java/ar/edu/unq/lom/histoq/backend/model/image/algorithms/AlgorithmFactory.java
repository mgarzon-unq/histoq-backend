package ar.edu.unq.lom.histoq.backend.model.image.algorithms;

import java.util.Arrays;

public class AlgorithmFactory {

    public static StitchingAlgorithm getStitchingAlgorithm(String from) {
        return (StitchingAlgorithm)instantiateOneOf(StitchingAlgorithm.class,from);
    }

    public static ScaleReferenceDetectionAlgorithm getScaleReferenceDetectionAlgorithm(String from) {
        return (ScaleReferenceDetectionAlgorithm)instantiateOneOf(ScaleReferenceDetectionAlgorithm.class,from);
    }

    public static TissueAnalysisAlgorithm getTissueAnalysisAlgorithm(String from) {
        return (TissueAnalysisAlgorithm)instantiateOneOf(TissueAnalysisAlgorithm.class,from);
    }

    private static Object instantiateOneOf(Class implementedInterface, String from) {
        try {
            Class algorithmClass = Class.forName(from);

            if( Arrays.stream(algorithmClass.getInterfaces()).noneMatch(p->p==implementedInterface) )
                throw new IllegalArgumentException(from + ": unimplemented "+implementedInterface.getSimpleName() + " interface.");

            return algorithmClass.getConstructors()[0].newInstance();
        }
        catch( Exception e ) {
            // config initializer will trap the exception...
        }
        return null;
    }
}
