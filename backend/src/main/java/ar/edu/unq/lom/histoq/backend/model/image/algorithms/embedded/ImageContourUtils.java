package ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImageContourUtils {
    private static final int NEXT_IN_LEVEL_OFFSET       = 0;
    private static final int PREVIOUS_IN_LEVEL_OFFSET   = 1;
    private static final int FIRST_CHILD_OFFSET         = 2;
    private static final int PARENT_OFFSET              = 3;
    public  static final int UNDEFINED_INDEX            = -1;

    public static Double sumContoursArea(List<MatOfPoint> contours) {
        return contours
                .stream()
                .mapToDouble( contour -> Imgproc.contourArea(contour,false))
                .sum();
    }

    public static Double sumContoursArea(List<MatOfPoint> contours, Mat hierarchy) {
        return sumLevelContoursArea(0, contours, hierarchy);
    }

    public static Double sumRootContoursArea(List<MatOfPoint> contours, Mat hierarchy) {
        int currentIndex = Math.min(0,hierarchy.cols()-1);
        List<MatOfPoint> rootContours = new ArrayList<>();

        while( currentIndex != UNDEFINED_INDEX ) {
            rootContours.add(contours.get(currentIndex));
            currentIndex = getNextIndexInHierarchyLevel(hierarchy,currentIndex);
        }

        return sumContoursArea(rootContours);
    }

    public static Double sumChildrenContoursArea(int parentIndex, List<MatOfPoint> contours, Mat hierarchy) {
        return sumLevelContoursArea(    getFirstChildIndexInHierarchy(hierarchy,parentIndex),
                contours, hierarchy );
    }

    public static boolean isRootContour(Mat hierarchy, int index) {
        return getParentInHierarchy(hierarchy,index)==UNDEFINED_INDEX;
    }

    public static int getParentInHierarchy(Mat hierarchy, int index) {
        return getHierarchyOffset(hierarchy,index,PARENT_OFFSET); // offset 3 of hierarchy info array is the "parent"....
    }

    public static int getFirstChildIndexInHierarchy(Mat hierarchy, int parentIndex) {
        return getHierarchyOffset(hierarchy,parentIndex,FIRST_CHILD_OFFSET); // offset 2 of hierarchy info array is the "first child"....
    }

    public static int getLastChildIndexInHierarchy(Mat hierarchy, int parentIndex) {
        int currentChild = getFirstChildIndexInHierarchy(hierarchy,parentIndex);
        int nextChild;
        while( currentChild != UNDEFINED_INDEX ) {
            nextChild = getNextIndexInHierarchyLevel(hierarchy,currentChild);
            if( nextChild == UNDEFINED_INDEX )
                return currentChild;
            currentChild = nextChild;
        }
        return -1;
    }

    public static int getNextIndexInHierarchyLevel(Mat hierarchy, int index) {
        return getHierarchyOffset(hierarchy,index, NEXT_IN_LEVEL_OFFSET); // offset 0 of hierarchy info array is "next" in the same level...
    }

    public static void addChildContour(List<MatOfPoint> contours, Mat hierarchy, int parentIndex, MatOfPoint newChildContour) {
        int lastChildIndex = getLastChildIndexInHierarchy(hierarchy,parentIndex);
        int newChildIndex = hierarchy.cols();

        // if parent already has a last child...
        if( lastChildIndex != UNDEFINED_INDEX )
            setHierarchyOffset(hierarchy,lastChildIndex,NEXT_IN_LEVEL_OFFSET,newChildIndex);   // change current last child info to point to new child as 'next in level'...
        else
            setHierarchyOffset(hierarchy,parentIndex,FIRST_CHILD_OFFSET,newChildIndex);   // change parent info to point to new child as 'first child'...

        // add new child contour..
        contours.add(newChildContour);

        // add new chilkd info to hierarchy matrix...
        Mat newChildInfo = new Mat(1,1,hierarchy.type());
        newChildInfo.put(0,0,new double[]{UNDEFINED_INDEX,lastChildIndex,UNDEFINED_INDEX,parentIndex});
        List<Mat> newChildInfoList = new ArrayList<>();
        newChildInfoList.add(hierarchy);
        newChildInfoList.add(newChildInfo);
        Core.hconcat(newChildInfoList,hierarchy);
    }

    public static double getMaxContourArea(List<MatOfPoint> contours) {
        return contours
                .stream()
                .mapToDouble( contour -> ImageContourUtils.getContourArea(contour) )
                .max()
                .orElse(0);
    }

    public static double getContourArea(MatOfPoint contour) {
        return Imgproc.contourArea(contour,false);
    }

    public static List<MatOfPoint> filterSmallContours(List<MatOfPoint> contours, double filterFactor) {
        double maxArea = getMaxContourArea(contours);

        return contours
                .stream()
                .filter( contour -> Imgproc.contourArea(contour,false) >= (maxArea*filterFactor) )
                .collect( Collectors.toList() );
    }

    public static MatOfPoint convertIndexesToPoints(MatOfPoint contour, MatOfInt indexes) {
        int[] arrIndex = indexes.toArray();
        Point[] arrContour = contour.toArray();
        Point[] arrPoints = new Point[arrIndex.length];

        for (int i=0;i<arrIndex.length;i++) {
            arrPoints[i] = arrContour[arrIndex[i]];
        }

        MatOfPoint hull = new MatOfPoint();
        hull.fromArray(arrPoints);
        return hull;
    }

    public static int getMainContourIndex(List<MatOfPoint> contours, Mat hierarchy) {
        int currentIndex = Math.min(hierarchy.cols()-1,0);
        int mainContourIndex = UNDEFINED_INDEX;
        Double maxContourArea = 0.0, contourArea;

        while( currentIndex != UNDEFINED_INDEX ) {
            contourArea = getContourArea(contours.get(currentIndex));
            if( contourArea > maxContourArea ) {
                maxContourArea = contourArea;
                mainContourIndex = currentIndex;
            }
            currentIndex = getNextIndexInHierarchyLevel(hierarchy,currentIndex);
        }

        return mainContourIndex;
    }

    private static Double sumLevelContoursArea(int firstIndex, List<MatOfPoint> contours, Mat hierarchy) {
        Double area = 0.0;
        int nextIndex = firstIndex;

        while( nextIndex != UNDEFINED_INDEX ) {
            area += sumContourArea(nextIndex, contours, hierarchy);
            nextIndex = getNextIndexInHierarchyLevel(hierarchy,nextIndex);
        }

        return area;
    }

    private static Double sumContourArea(int whichContour, List<MatOfPoint> contours, Mat hierarchy) {
        return contours.size() > 0  ?   Imgproc.contourArea(contours.get(whichContour),false) -
                sumChildrenContoursArea(whichContour,contours,hierarchy)
                :   0.0 ;
    }

    private static int getHierarchyOffset(Mat hierarchy, int index, int offset) {
        return hierarchy.cols() > index ?   (int)hierarchy.get(0,index)[offset]
                :   UNDEFINED_INDEX;
    }

    private static void setHierarchyOffset(Mat hierarchy, int index, int offset, double value) {
        double[] info = hierarchy.get(0,index);
        info[offset] = value;
        hierarchy.put(0,index,info);
    }
}
