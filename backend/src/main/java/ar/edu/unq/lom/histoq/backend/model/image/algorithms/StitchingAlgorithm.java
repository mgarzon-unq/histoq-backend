package ar.edu.unq.lom.histoq.backend.model.image.algorithms;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.exception.AlgorithmException;

import java.util.List;

public interface StitchingAlgorithm {
    public void stitchImageFiles(List<String> filePaths, String outputFilePath) throws AlgorithmException;
}
