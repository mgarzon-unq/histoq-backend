package ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.BaseAlgorithm;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.TissueAnalysisAlgorithm;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.TissueData;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded.util.ScalarJsonDeserializer;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded.util.ScalarJsonSerializer;
import ar.edu.unq.lom.histoq.backend.service.config.algorithms.embedded.EmbeddedTissueAnalysisAlgorithmConfig;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;
import java.util.List;


public class EmbeddedTissueAnalysisAlgorithm extends BaseAlgorithm<EmbeddedTissueAnalysisAlgorithmConfig>
                                                implements TissueAnalysisAlgorithm {

    public EmbeddedTissueAnalysisAlgorithm() {
        super(EmbeddedTissueAnalysisAlgorithmConfig.class);
        addCustomJsonSerializers();
    }

    private void addCustomJsonSerializers() {
        addJsonSerializer(Scalar.class, new ScalarJsonSerializer());
        addJsonDeserializer(Scalar.class, new ScalarJsonDeserializer());
    }

    @Override
    public TissueData findTissueAreas(String inputFilePath, String outputFilePath) {
        EmbeddedTissueData tissue = new EmbeddedTissueData();
        tissue.setSourceImage(Imgcodecs.imread(inputFilePath));

        // find tissue area...
        tissue = findTissueArea(tissue);

        // find viable tissue area and necrotic tissue area...
        tissue = findViableVsNecroticTissueArea(tissue);

        // draw tissue areas contours...
        drawTissueAreasContours(tissue);

        buildTissueAreasTransparentOverlay(tissue);

        // write result image file...
        Imgcodecs.imwrite( outputFilePath, tissue.getSourceImage() );

        return tissue;
    }

    @Override
    public Map<String, String> getDefaultProcessingParameters() {
        return new HashMap<String,String>(){{
            put("ViableTissueAreaMinHSVInRange",getValueAsJson(getConfig().getViableTissueAreaMinHSVInRange()));
            put("ViableTissueAreaMaxHSVInRange",getValueAsJson(getConfig().getViableTissueAreaMaxHSVInRange()));
        }};
    }

    @Override
    public void resetProcessingParametersToDefault() {
        super.resetProcessingParametersToDefault();
    }

    @Override
    public void setProcessingParameterValue(String name, String value) {
        super.setProcessingParameterValue(name,value);
    }

    private EmbeddedTissueData findTissueArea(EmbeddedTissueData tissue)
    {
        // convert to HSV color space...
        Mat imageHSV = new Mat();
        Imgproc.cvtColor(tissue.getSourceImage(), imageHSV, Imgproc.COLOR_BGR2HSV);
        List<Mat> hsvChannels = new ArrayList<>();
        Core.split(imageHSV, hsvChannels);
        Mat sChannel = hsvChannels.get(1);

        // blur the saturation channel...
        Imgproc.GaussianBlur(sChannel, sChannel, getConfig().getTissueAreaGaussianBlurKernelSize(), 0);

        // find threshold...
        Mat imageWithThreshold = new Mat();
        Imgproc.threshold(sChannel,
                imageWithThreshold,
                getConfig().getTissueAreaThresholdValue(),
                getConfig().getTissueAreaThresholdMaxValue(),
                Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        // find the contours...
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(   imageWithThreshold,
                contours,
                hierarchy,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE );

        // filter child contours...
        tissue.setTotalTissueAreaContours( ImageContourUtils.filterSmallContours(contours,getConfig().getTissueAreaBackgroundFilterFactor()));
        // build total tissue area mask...
        tissue.setTotalTissueAreaMask(new Mat(imageWithThreshold.size(),imageWithThreshold.type(),new Scalar(0,0,0)));
        Imgproc.fillPoly(tissue.getTotalTissueAreaMask(),tissue.getTotalTissueAreaContours(),new Scalar(255,255,255));

        return tissue;
    }

    private EmbeddedTissueData findViableVsNecroticTissueArea(EmbeddedTissueData tissue) {
        findViableTissueArea(tissue);
        findNecroticTissueArea(tissue);
        return tissue;
    }

    private EmbeddedTissueData findViableTissueArea(EmbeddedTissueData tissue)
    {
        // convert source to HSV color space...
        Mat imageHSV = new Mat();
        Imgproc.cvtColor(tissue.getSourceImage(), imageHSV, Imgproc.COLOR_BGR2HSV);

        // blur to remove noise...
        Mat imageBlur = new Mat();
        Imgproc.GaussianBlur(   imageHSV,
                imageBlur,
                getConfig().getTissueAreaGaussianBlurKernelSize(),
                0 );

        // create hematoxylin mask...
        Mat hematoxylinMask = new Mat();
        Core.inRange(   imageBlur,
                (Scalar) getProcessingParameterValue("ViableTissueAreaMinHSVInRange", Scalar.class),
                (Scalar) getProcessingParameterValue("ViableTissueAreaMaxHSVInRange", Scalar.class),
                hematoxylinMask );

        // also remove everything outside the total tissue area from the mask...
        Mat justHematoxylinAreaMask = new Mat();
        Core.bitwise_and(hematoxylinMask,tissue.getTotalTissueAreaMask(),justHematoxylinAreaMask);

        // find the contours in the resulting mask...
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours( justHematoxylinAreaMask,
                contours,
                hierarchy,
                Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE );

        tissue.setViableTissueAreaContours(contours);
        tissue.setViableTissueAreaContoursHierarchy(hierarchy);

        return tissue;
    }

    private EmbeddedTissueData findNecroticTissueArea(EmbeddedTissueData tissue) {

        findUnenclosedNecroticTissueAreas(tissue);

        // if there is so much undefined tissue area outside [viable with enclosed necrotic tissue + unenclosed necrotic tissue] area ...
        Double totalTissueArea = tissue.getTotalTissueArea();
        if(  totalTissueArea - (tissue.getViableTissueArea() + tissue.getNecroticTissueArea())
                > (totalTissueArea * getConfig().getNecroticTissueAreaMinDetectedToTotalFactor()) ) {
            // then, unenclosed necrotic area is considered as total - [viable with enclosed necrotic tissue]...
            forceUnenclosedNecroticTissueArea(tissue);
        }

        return tissue;
    }

    private EmbeddedTissueData findUnenclosedNecroticTissueAreas(EmbeddedTissueData tissue) {
        List<MatOfPoint> viableTissueAreaContours   = tissue.getViableTissueAreaContours();
        Mat viableTissueAreaHierarchy               = tissue.getViableTissueAreaContoursHierarchy();
        int currentViableTissueContourIndex         = Math.min(0,viableTissueAreaHierarchy.cols()-1);
        final int mainContourIndex                  = ImageContourUtils.getMainContourIndex(viableTissueAreaContours,viableTissueAreaHierarchy);
        final Double mainContourArea                = mainContourIndex != ImageContourUtils.UNDEFINED_INDEX ? ImageContourUtils.getContourArea(viableTissueAreaContours.get(mainContourIndex)) : 0.0;
        Mat contourMask                             = tissue.getTotalTissueAreaMask().clone();
        Mat summaryUnenclosedChildrenMask           = Mat.zeros(tissue.getTotalTissueAreaMask().size(),tissue.getTotalTissueAreaMask().type());
        Mat hullMask                                = new Mat(tissue.getTotalTissueAreaMask().size(),tissue.getTotalTissueAreaMask().type());
        Mat unenclosedChildrenMask                  = new Mat();
        MatOfInt hull                               = new MatOfInt();
        List<MatOfPoint> hullPoints                 = new ArrayList<>();
        List<MatOfPoint> contourPoints              = new ArrayList<>();
        List<MatOfPoint> unenclosedChildrenContours = new ArrayList<>();
        Mat unenclosedChildrenHierarchy             = new Mat();
        Scalar contourMaskBackground                = new Scalar(255,255,255);
        Scalar contourMaskForeground                = new Scalar(0,0,0);
        Scalar hullMaskBackground                   = new Scalar(0,0,0);
        Scalar hullMaskForeground                   = new Scalar(255,255,255);

        contourPoints.add(null);
        hullPoints.add(null);

        // iterate only root viable tissue area contours...
        while( currentViableTissueContourIndex != ImageContourUtils.UNDEFINED_INDEX ) {
            // prepare contour mask...
            contourPoints.set(0,viableTissueAreaContours.get(currentViableTissueContourIndex));
            Imgproc.fillPoly(contourMask,contourPoints,contourMaskForeground);

            // get convext hull of the contour and prepare hull mask...
            Imgproc.convexHull(viableTissueAreaContours.get(currentViableTissueContourIndex),hull);
            hullMask.setTo(hullMaskBackground);
            hullPoints.set(0,ImageContourUtils.convertIndexesToPoints(viableTissueAreaContours.get(currentViableTissueContourIndex),hull));
            Imgproc.fillPoly(hullMask,hullPoints,hullMaskForeground);

            // merge contour mask AND hull mask tu reveal unenclosed children shapes...
            Core.bitwise_and(contourMask,hullMask,unenclosedChildrenMask);

            // find unenclosed children contours...
            unenclosedChildrenContours.clear();
            Imgproc.findContours(   unenclosedChildrenMask,
                    unenclosedChildrenContours,
                    unenclosedChildrenHierarchy,
                    Imgproc.RETR_EXTERNAL,
                    Imgproc.CHAIN_APPROX_SIMPLE );
            // add those wih meaningful areas to the summary unenclosed children mask...
            unenclosedChildrenContours.forEach( unenclosedChildContour -> {
                if( ImageContourUtils.getContourArea(unenclosedChildContour) >=
                        (mainContourArea*getConfig().getNecroticTissueAreaUnenclosedChildrenFilterFactor()) ) {
                    List<MatOfPoint> unenclosedChildPoints = new ArrayList<>();
                    unenclosedChildPoints.add(unenclosedChildContour);
                    Imgproc.fillPoly(summaryUnenclosedChildrenMask,unenclosedChildPoints,hullMaskForeground);
                }
            });

            // clear contour mask...
            Imgproc.fillPoly(contourMask,contourPoints,contourMaskBackground);

            // move to next root viable tissue area contour...
            currentViableTissueContourIndex = ImageContourUtils.getNextIndexInHierarchyLevel(viableTissueAreaHierarchy,currentViableTissueContourIndex);
        }

        // now find contours in the summary unenclosed children mask...
        unenclosedChildrenContours.clear();
        unenclosedChildrenHierarchy = new Mat();
        Imgproc.findContours(   summaryUnenclosedChildrenMask,
                unenclosedChildrenContours,
                unenclosedChildrenHierarchy,
                Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE );
        tissue.setUnenclosedNecroticTissueAreaContours(unenclosedChildrenContours);
        tissue.setUnenclosedNecroticTissueAreaContoursHierarchy(unenclosedChildrenHierarchy);

        return tissue;
    }

    private EmbeddedTissueData forceUnenclosedNecroticTissueArea(EmbeddedTissueData tissue) {
        List<MatOfPoint> viableTissueAreaContours   = tissue.getViableTissueAreaContours();
        Mat viableTissueAreaHierarchy               = tissue.getViableTissueAreaContoursHierarchy();
        int currentViableTissueContourIndex         = Math.min(0,viableTissueAreaHierarchy.cols()-1);
        Mat necroticAreaMask                        = tissue.getTotalTissueAreaMask().clone();
        List<MatOfPoint> contourPoints              = new ArrayList<>();
        Scalar contourMaskForeground                = new Scalar(0,0,0);

        contourPoints.add(null);

        // iterate only root viable tissue area contours...
        while( currentViableTissueContourIndex != ImageContourUtils.UNDEFINED_INDEX ) {
            // add contour to the mask...
            contourPoints.set(0, viableTissueAreaContours.get(currentViableTissueContourIndex));
            Imgproc.fillPoly(necroticAreaMask, contourPoints, contourMaskForeground);

            // move to next root viable tissue area contour...
            currentViableTissueContourIndex = ImageContourUtils.getNextIndexInHierarchyLevel(viableTissueAreaHierarchy, currentViableTissueContourIndex);
        }

        // find the necrotic contours in the resulting mask...
        List<MatOfPoint> necroticAreaContours = new ArrayList<>();
        Mat necroticAreaHierarchy = new Mat();
        Imgproc.findContours(   necroticAreaMask,
                necroticAreaContours,
                necroticAreaHierarchy,
                Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE );
        tissue.setUnenclosedNecroticTissueAreaContours(necroticAreaContours);
        tissue.setUnenclosedNecroticTissueAreaContoursHierarchy(necroticAreaHierarchy);

        return tissue;
    }

    private void drawContours(Mat outputImage, List<MatOfPoint> contours, Scalar color, int contourIndex) {
        Imgproc.drawContours(   outputImage,
                contours,
                contourIndex,
                color,
                getConfig().getDrawContourThickness(),
                Imgproc.LINE_4 );
    }

    private void drawTissueAreasContours(EmbeddedTissueData tissue) {

        drawEnclosedTissueAreasContours(    tissue.getSourceImage(),
                tissue.getViableTissueAreaContours(),
                tissue.getViableTissueAreaContoursHierarchy(),
                0,
                true,
                ImageContourUtils.getMaxContourArea(tissue.getViableTissueAreaContours()));

        drawEnclosedTissueAreasContours(    tissue.getSourceImage(),
                tissue.getUnenclosedNecroticTissueAreaContours(),
                tissue.getUnenclosedNecroticTissueAreaContoursHierarchy(),
                0,
                false,
                ImageContourUtils.getMaxContourArea(tissue.getUnenclosedNecroticTissueAreaContours()));

        drawContours(   tissue.getSourceImage(),
                tissue.getTotalTissueAreaContours(),
                getConfig().getTissueAreaContourColor(),
                -1);
    }

    private void drawEnclosedTissueAreasContours(Mat image, List<MatOfPoint> contours, Mat hierarchy, int parentIndex, boolean isViableTissue, Double maxContourArea) {
        int currentIndex = Math.min(parentIndex,hierarchy.cols()-1);

        while( currentIndex != ImageContourUtils.UNDEFINED_INDEX ) {
            if( Imgproc.contourArea(contours.get(currentIndex),false) >=
                    (maxContourArea*getConfig().getViableTissueAreaFilterFactor()) ) {

                drawContours(   image,
                        contours,
                        selectContourColor(isViableTissue),
                        currentIndex );

                drawEnclosedTissueAreasContours(image,
                        contours,
                        hierarchy,
                        ImageContourUtils.getFirstChildIndexInHierarchy(hierarchy,currentIndex),
                        !isViableTissue,
                        maxContourArea );
            }
            currentIndex = ImageContourUtils.getNextIndexInHierarchyLevel(hierarchy,currentIndex);
        }
    }

    private Scalar selectContourColor(boolean isViableTissue) {
        return isViableTissue   ? getConfig().getViableTissueAreaContourColor()
                : getConfig().getNecroticTissueAreaContourColor();
    }

    private void buildTissueAreasTransparentOverlay(EmbeddedTissueData tissue) {
        Mat overlay = tissue.getSourceImage().clone();

        buildEnclosedTissueAreasTransparentOverlay( overlay,
                tissue.getUnenclosedNecroticTissueAreaContours(),
                tissue.getUnenclosedNecroticTissueAreaContoursHierarchy(),
                0,
                false,
                ImageContourUtils.getMaxContourArea(tissue.getUnenclosedNecroticTissueAreaContours()) );

        buildEnclosedTissueAreasTransparentOverlay( overlay,
                tissue.getViableTissueAreaContours(),
                tissue.getViableTissueAreaContoursHierarchy(),
                0,
                true,
                ImageContourUtils.getMaxContourArea(tissue.getViableTissueAreaContours()) );

        Double alpha = getConfig().getOverlayTransparency();
        Core.addWeighted(overlay,alpha,tissue.getSourceImage(),1-alpha,0,tissue.getSourceImage());

    }

    private void buildEnclosedTissueAreasTransparentOverlay(Mat overlay, List<MatOfPoint> contours, Mat hierarchy, int parentIndex, boolean isViableTissue, Double maxContourArea) {
        int currentIndex = Math.min(parentIndex,hierarchy.cols()-1);
        List<MatOfPoint> contourPoints = new ArrayList<>();

        contourPoints.add(null);

        while( currentIndex != ImageContourUtils.UNDEFINED_INDEX ) {
            if( Imgproc.contourArea(contours.get(currentIndex),false) >=
                    (maxContourArea*getConfig().getViableTissueAreaFilterFactor()) ) {

                contourPoints.set(0, contours.get(currentIndex));
                Imgproc.fillPoly( overlay, contourPoints, selectOverlayColor(isViableTissue));

                buildEnclosedTissueAreasTransparentOverlay(overlay,
                        contours,
                        hierarchy,
                        ImageContourUtils.getFirstChildIndexInHierarchy(hierarchy,currentIndex),
                        !isViableTissue,
                        maxContourArea );
            }
            currentIndex = ImageContourUtils.getNextIndexInHierarchyLevel(hierarchy,currentIndex);
        }
    }

    private Scalar selectOverlayColor(boolean isViableTissue) {
        return isViableTissue ? getConfig().getViableTissueAreaOverlayColor()
                : getConfig().getNecroticTissueAreaOverlayColor();
    }

}

