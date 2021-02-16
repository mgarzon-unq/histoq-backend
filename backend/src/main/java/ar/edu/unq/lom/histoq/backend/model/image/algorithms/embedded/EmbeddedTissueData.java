package ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.TissueData;
import lombok.Data;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import java.util.List;

@Data
public class EmbeddedTissueData implements TissueData {
    private Mat                 sourceImage;
    private List<MatOfPoint>    totalTissueAreaContours;
    private Mat                 totalTissueAreaMask;
    private List<MatOfPoint>    viableTissueAreaContours;
    private Mat                 viableTissueAreaContoursHierarchy;
    private List<MatOfPoint>    unenclosedNecroticTissueAreaContours;
    private Mat                 unenclosedNecroticTissueAreaContoursHierarchy;

    @Override
    public Double getTotalArea() {
        Mat sourceImage = getSourceImage();
        return sourceImage!=null ?  (double) (sourceImage.width() * sourceImage.height())
                                 :  0.0;
    }

    @Override
    public Double getTotalTissueArea() {
        return ImageContourUtils.sumContoursArea(getTotalTissueAreaContours());
    }

    @Override
    public Double getViableTissueArea() {
        return  ImageContourUtils.sumContoursArea(  getViableTissueAreaContours(),
                                                    getViableTissueAreaContoursHierarchy()  );
    }

    @Override
    public Double getNecroticTissueArea() {
        return  sumEnclosedNecroticAreas() +
                ImageContourUtils.sumContoursArea(  getUnenclosedNecroticTissueAreaContours(),
                        getUnenclosedNecroticTissueAreaContoursHierarchy());
    }

    private Double sumEnclosedNecroticAreas() { // sum necrotic areas enclosed into main viable areas...
        Double area = 0.0;
        int currentIndex = 0;

        while( currentIndex != -1 ) {
            area += ImageContourUtils.sumChildrenContoursArea(currentIndex,getViableTissueAreaContours(),getViableTissueAreaContoursHierarchy());
            currentIndex = ImageContourUtils.getNextIndexInHierarchyLevel(getViableTissueAreaContoursHierarchy(),currentIndex);
        }

        return area;
    }

}
