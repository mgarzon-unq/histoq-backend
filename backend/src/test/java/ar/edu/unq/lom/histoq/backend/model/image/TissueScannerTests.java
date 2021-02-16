package ar.edu.unq.lom.histoq.backend.model.image;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.*;
import ar.edu.unq.lom.histoq.backend.service.config.ApplicationConfigProperties;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TissueScannerTests {

    private TissueScanner tissueScanner;

    @Mock
    private ApplicationConfigProperties applicationConfigProperties;
    @Mock
    private StitchingAlgorithm stitchingAlgorithm;
    @Mock
    private ScaleReferenceDetectionAlgorithm scaleReferenceDetectionAlgorithm;
    @Mock
    private TissueAnalysisAlgorithm tissueAnalysisAlgorithm;

    private final Path rootFolder = Paths.get("./src/test/resources/images");
    private final String stitchedImagePrefix = "stitched-";
    private final String scannedImagePrefix = "scanned-";

    @BeforeEach
    public void setUp() {
        this.tissueScanner = new TissueScanner( this.applicationConfigProperties);

        when(this.applicationConfigProperties.getRootFolder()).thenReturn(this.rootFolder.toString());
        when(this.applicationConfigProperties.getStitchedImagePrefix()).thenReturn(this.stitchedImagePrefix);
        when(this.applicationConfigProperties.getScannedImagePrefix()).thenReturn(this.scannedImagePrefix);
        when(this.applicationConfigProperties.getStitchingAlgorithm()).thenReturn(this.stitchingAlgorithm);
        when(this.applicationConfigProperties.getScaleReferenceDetectionAlgorithm()).thenReturn(this.scaleReferenceDetectionAlgorithm);
        when(this.applicationConfigProperties.getTissueAnalysisAlgorithm()).thenReturn(this.tissueAnalysisAlgorithm);
    }

    @Test
    public void stitchImageFilesTest() {
        ImageBatch imageBatch = mock(ImageBatch.class);
        Long imageBatchId = 1L;
        ImageFile imageFile = mock(ImageFile.class);
        List<ImageFile> imageFiles = Arrays.asList(new ImageFile[]{imageFile});
        String groupName = "1378";

        when(imageFile.getName()).thenReturn("1378-1.png");
        when(imageFile.getBatch()).thenReturn(imageBatch);
        when(imageBatch.getId()).thenReturn(imageBatchId);

        String stitchedFileFullPath = this.rootFolder.resolve( imageBatchId.toString() + "/" +this.stitchedImagePrefix+groupName+"."+ FilenameUtils.getExtension(imageFile.getName())).toString();
        ImageFile stitchedImageFile = this.tissueScanner.stitchImageFiles(imageFiles,groupName);
        List<String> listOfFileNames = imageFiles.stream().map(imFile->this.rootFolder.resolve( imageBatchId.toString() + "/" +imFile.getName()).toString()).collect(Collectors.toList());

        assertEquals(this.stitchedImagePrefix+groupName+"."+ FilenameUtils.getExtension(imageFile.getName()), stitchedImageFile.getName());

        verify(this.stitchingAlgorithm,times(1)).stitchImageFiles(listOfFileNames,stitchedFileFullPath);
    }

    @Test
    public void processImageFileTest() {
        ImageBatch imageBatch = mock(ImageBatch.class);
        Long imageBatchId = 1L;
        ImageFile imageFile = mock(ImageFile.class);
        ImageScaleReference scaleReference = mock(ImageScaleReference.class);
        TissueData tissueData = mock(TissueData.class);
        String  measurementUnit ="um", imageFileName = "image.png",
                inputImageFileFullPath = this.rootFolder.resolve( imageBatchId.toString() + "/" + imageFileName).toString(),
                outputImageFileFullPath = this.rootFolder.resolve( imageBatchId.toString() + "/" + scannedImagePrefix + imageFileName).toString();
        Integer scaleValue = 1000, scalePixels = 300;
        Double totalArea = 4000.0, totalTissueArea = 2000.0, viableTissueArea = 1500.0, necroticTissueArea = 500.0;

        when(imageFile.getName()).thenReturn(imageFileName);
        when(imageFile.getBatch()).thenReturn(imageBatch);
        when(imageBatch.getId()).thenReturn(imageBatchId);
        when(scaleReference.getMeasurementUnit()).thenReturn(measurementUnit);
        when(scaleReference.getScaleValue()).thenReturn(scaleValue);
        when(scaleReference.getScalePixels()).thenReturn(scalePixels);
        when(tissueData.getTotalArea()).thenReturn(totalArea);
        when(tissueData.getTotalTissueArea()).thenReturn(totalTissueArea);
        when(tissueData.getViableTissueArea()).thenReturn(viableTissueArea);
        when(tissueData.getNecroticTissueArea()).thenReturn(necroticTissueArea);

        when(this.scaleReferenceDetectionAlgorithm.findScaleReference(anyString())).thenReturn(scaleReference);
        when(this.tissueAnalysisAlgorithm.findTissueAreas(anyString(),anyString())).thenReturn(tissueData);

        Image image = this.tissueScanner.processImageFile(imageFile);

        verify(this.scaleReferenceDetectionAlgorithm,times(1)).findScaleReference(inputImageFileFullPath);
        verify(this.tissueAnalysisAlgorithm,times(1)).findTissueAreas(inputImageFileFullPath,outputImageFileFullPath);

        assertEquals(this.scannedImagePrefix+imageFileName,image.getName());
        assertEquals(measurementUnit, image.getMeasurementUnit());
        assertEquals(scaleValue,image.getScaleValue());
        assertEquals(scalePixels,image.getScalePixels());
        assertEquals(totalArea,image.getTotalArea());
        assertEquals(totalTissueArea,image.getTotalTissueArea());
        assertEquals(viableTissueArea,image.getViableTissueArea());
        assertEquals(necroticTissueArea,image.getNecroticTissueArea());
    }

}
