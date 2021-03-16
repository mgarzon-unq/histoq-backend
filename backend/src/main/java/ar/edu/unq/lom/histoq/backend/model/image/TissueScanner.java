package ar.edu.unq.lom.histoq.backend.model.image;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.ImageScaleReference;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.TissueAnalysisAlgorithm;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.TissueData;
import ar.edu.unq.lom.histoq.backend.model.image.exception.ImageScannerException;
import ar.edu.unq.lom.histoq.backend.service.config.ApplicationConfigProperties;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TissueScanner implements ImageScanner {

    private final ApplicationConfigProperties applicationConfigProperties;

    public TissueScanner(ApplicationConfigProperties applicationConfigProperties) {
        this.applicationConfigProperties = applicationConfigProperties;
    }

    @Override
    public Image processImageFile(ImageFile imageFile) {
        return processImageFile(imageFile,true);
    }

    public ImageFile stitchImageFiles(List<ImageFile> imageFiles, String fileGroupName) {
        try {
            String batchFolder = imageFiles.get(0).getBatch().getId().toString();
            ImageFile outputImageFile = new ImageFile();
            outputImageFile.setName(applicationConfigProperties.getStitchedImagePrefix()
                    + fileGroupName + "." + getFileExtension(imageFiles.get(0).getName()));

            applicationConfigProperties.getStitchingAlgorithm()
                    .stitchImageFiles(  imageFiles.stream().map( imageFile -> getFullFilePath(batchFolder + "/" + imageFile.getName())).collect(Collectors.toList()),
                            getFullFilePath(batchFolder + "/" + outputImageFile.getName()));

            return outputImageFile;
        }
        catch(Exception e) {
            throw new ImageScannerException("image.scanner-stitching-exception",
                    new String[]{fileGroupName,e.getMessage()});
        }
    }

    @Override
    public Image previewProcessedImageFile(ImageFile imageFile) {
        return processImageFile(imageFile,false);
    }

    @Override
    public Map<String,String> getDefaultProcessingParameters() {
        return getTissueAnalysisAlgorithm().getDefaultProcessingParameters();
    }

    private Image processImageFile(ImageFile imageFile, boolean findScaleReference) {
        try {
            String batchFolder = imageFile.getBatch().getId().toString();
            String inputFileName = batchFolder + "/" + imageFile.getName();
            String fullInputFilePath = getFullFilePath(inputFileName);
            String processedFileName = applicationConfigProperties.getScannedImagePrefix() + imageFile.getName();
            String fullOutputFilePath = getFullFilePath( batchFolder + "/" + processedFileName);
            Image newImage = new Image();
            newImage.setName(processedFileName);

            ImageScaleReference imageScaleReference = findScaleReference ? applicationConfigProperties.getScaleReferenceDetectionAlgorithm().findScaleReference(fullInputFilePath) :
                                                            new ImageScaleReference();
            newImage.setMeasurementUnit(imageScaleReference.getMeasurementUnit());
            newImage.setScaleValue(imageScaleReference.getScaleValue());
            newImage.setScalePixels(imageScaleReference.getScalePixels());

            setTissueAnalysisAlgorithmCustomizedParameters(imageFile);
            TissueData tissue = getTissueAnalysisAlgorithm().findTissueAreas(fullInputFilePath,fullOutputFilePath);
            newImage.setTotalArea(tissue.getTotalArea());
            newImage.setTotalTissueArea(tissue.getTotalTissueArea());
            newImage.setViableTissueArea(tissue.getViableTissueArea());
            newImage.setNecroticTissueArea(tissue.getNecroticTissueArea());

            ImageFile outputImageFile = new ImageFile();
            outputImageFile.setBatch(imageFile.getBatch());
            outputImageFile.setGenerated(true);
            outputImageFile.setName(processedFileName);
            outputImageFile.setImage(newImage);
            outputImageFile.getBatch().addImageFile(outputImageFile);
            newImage.addImageFile(outputImageFile);

            return newImage;
        }
        catch(Exception e) {
            throw new ImageScannerException("image.scanner-process-exception",
                    new String[]{imageFile.getName(),e.getMessage()});
        }
    }

    private Path getRootFolder() {
        return Paths.get(applicationConfigProperties.getRootFolder());
    }

    private String getFullFilePath(String fileName) {
        return getRootFolder().resolve(fileName).toString();
    }

    private String getFileExtension(String fileName) {
        return FilenameUtils.getExtension(fileName);
    }

    private TissueAnalysisAlgorithm getTissueAnalysisAlgorithm() { return applicationConfigProperties.getTissueAnalysisAlgorithm(); }

    private void setTissueAnalysisAlgorithmCustomizedParameters(ImageFile imageFile) {
        TissueAnalysisAlgorithm algorithm = getTissueAnalysisAlgorithm();
        algorithm.resetProcessingParametersToDefault();
        imageFile.getCustomProcessingParameters().forEach( p -> {
           algorithm.setProcessingParameterValue( p.getName(), p.getValue() );
        });
    }

}
