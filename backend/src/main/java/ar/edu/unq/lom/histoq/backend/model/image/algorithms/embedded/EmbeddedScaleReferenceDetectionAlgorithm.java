package ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded.util.Pair;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.exception.AlgorithmException;
import ar.edu.unq.lom.histoq.backend.service.config.algorithms.embedded.EmbeddedScaleReferenceDetectionAlgorithmConfig;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.BaseAlgorithm;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.ScaleReferenceDetectionAlgorithm;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.ptr.PointerByReference;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.TessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Word;
import net.sourceforge.tess4j.util.ImageIOHelper;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.util.CollectionUtils;
import javax.imageio.ImageIO;
import javax.validation.constraints.Null;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class EmbeddedScaleReferenceDetectionAlgorithm extends BaseAlgorithm<EmbeddedScaleReferenceDetectionAlgorithmConfig>
                                                        implements ScaleReferenceDetectionAlgorithm {

    public EmbeddedScaleReferenceDetectionAlgorithm() {
        super(EmbeddedScaleReferenceDetectionAlgorithmConfig.class);
    }

    @Override
    public EmbeddedImageScaleReference findScaleReference(String filePath) {
        EmbeddedImageScaleReference imageScaleReference = new EmbeddedImageScaleReference();
        Mat sourceImage = Imgcodecs.imread(filePath);

        findScaleReferenceText(sourceImage,imageScaleReference);
        findScaleReferenceLine(sourceImage,imageScaleReference);

        return imageScaleReference;
    }

    private void findScaleReferenceText(Mat sourceImage, EmbeddedImageScaleReference imageScaleReference) {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(getConfig().getTesseractDataPath());
        tesseract.setLanguage(getConfig().getTesseractLanguage());
        tesseract.setPageSegMode(getConfig().getTesseractPageSegmentationMode());
        tesseract.setOcrEngineMode(getConfig().getTesseractOcrEngineMode());

        // convert source to gray scale...
        Mat imageGray = new Mat();
        Imgproc.cvtColor(sourceImage, imageGray, Imgproc.COLOR_BGR2GRAY);

        // apply threshold to keep only pure white pixels scale reference, then invert to black...
        Mat imageWithThreshold = new Mat();
        Imgproc.threshold(  imageGray,
                imageWithThreshold,
                getConfig().getReferenceTextThresholdValue(),
                getConfig().getReferenceTextThresholdMaxValue(),
                Imgproc.THRESH_BINARY_INV );

        // send image to Tesseract...
        try {

            // PATCH to prevent Tesseract.getWords() internal NullPointerException that (for some reason) cant be caught by the try-catch block...
            TesseractHelper tessHelper = new TesseractHelper(getConfig().getTesseractDataPath(),
                    getConfig().getTesseractLanguage(),
                    getConfig().getTesseractOcrEngineMode(),
                    getConfig().getTesseractPageSegmentationMode());

            BufferedImage bufferedImage = mat2BufferedImage(imageWithThreshold);
            boolean tesseractWillFail = tessHelper.getWordsWillFail(bufferedImage, ITessAPI.TessPageIteratorLevel.RIL_TEXTLINE);

            List<Word> textLines = !tesseractWillFail ?
                    tesseract.getWords(bufferedImage, ITessAPI.TessPageIteratorLevel.RIL_TEXTLINE) :
                    null;
            //List<Word> textLines = tesseract.getWords(bufferedImage, ITessAPI.TessPageIteratorLevel.RIL_TEXTLINE);

            // PATCH END

            // process detected text lines...
            if( !CollectionUtils.isEmpty(textLines) ) {
                textLines.stream()
                        .filter( textLine -> cleanTextLine(textLine.getText()).matches(getConfig().getReferenceTextMatchRegex()))
                        .forEach( textLine -> {
                            String[] textTokens = cleanTextLine(textLine.getText()).split(getConfig().getReferenceTextSplitRegex());
                            imageScaleReference.setScaleValue(Integer.parseInt(textTokens[0]));
                            imageScaleReference.setMeasurementUnit(textTokens[1]);
                            imageScaleReference.setTextRectangle(rectangleToRect(textLine.getBoundingBox()));
                        });
            }
        }
        catch(Exception e) {
            throw new AlgorithmException("embedded.scale.reference.detection.algorithm-text-exception",
                    new String[]{e.getMessage()});
        }
    }

    private void findScaleReferenceLine(Mat sourceImage, EmbeddedImageScaleReference imageScaleReference)
    {
        // convert to gray...
        Mat imageGray = new Mat();
        Imgproc.cvtColor(sourceImage, imageGray, Imgproc.COLOR_BGR2GRAY);

        // blur the grey image...
        Mat blurGray = new Mat();
        Imgproc.GaussianBlur(   imageGray,
                blurGray,
                getConfig().getReferenceLineGaussianBlurKernelSize(),
                0);

        // edge detection using Canny...
        Mat edges = new Mat();
        Imgproc.Canny(  blurGray,
                edges,
                getConfig().getReferenceLineCannyThreshold1(),
                getConfig().getReferenceLineCannyThreshold2()    );

        // use HoughLinesP to get the lines....
        Mat lines = new Mat();
        Imgproc.HoughLinesP(    edges,
                lines,
                getConfig().getReferenceLineHoughlinespRho(),
                getConfig().getReferenceLineHoughlinespTheta(),
                getConfig().getReferenceLineHoughlinespThreshold(),
                getConfig().getReferenceLineHoughlinespMinLineLength(),
                getConfig().getReferenceLineHoughlinespMaxLineGap()  );

        // filter straight lines, compute max line length and min distance to text....
        List<Pair<Point,Point>> straightLines = new ArrayList<>();
        int maxLineLength = 0, lineLength;
        int minLineDistanceToReferenceText = Integer.MAX_VALUE,
                lineDistanceToReferenceText;

        for (int i = 0; i < lines.rows(); i++) {
            double[] val = lines.get(i, 0);

            Point lineBegin = new Point(val[0], val[1]);
            Point lineEnd = new Point(val[2], val[3]);

            if( isStraightLine(lineBegin,lineEnd) ) {
                straightLines.add(new Pair<>(lineBegin,lineEnd));

                lineLength = computeLineLength(lineBegin,lineEnd);
                if( lineLength > maxLineLength ) maxLineLength = lineLength;

                lineDistanceToReferenceText = computeLineLength(imageScaleReference.getTextRectXY(),lineBegin);
                if( lineDistanceToReferenceText < minLineDistanceToReferenceText )
                    minLineDistanceToReferenceText = lineDistanceToReferenceText;
            }
        }

        // Filter closest line to scale reference text...
        // If no scale reference text, filter longest line...
        final int maxLength = maxLineLength;
        final int minDistance = minLineDistanceToReferenceText;
        straightLines
                .stream().filter( pointPair -> {
            if( imageScaleReference.found() )
                return computeLineLength(imageScaleReference.getTextRectXY(),pointPair.getKey()) == minDistance;
            return computeLineLength(pointPair.getKey(),pointPair.getValue()) == maxLength;
        } )
                .forEach( pointPair -> {
                    imageScaleReference.setScalePixels(
                            computeLineLength(pointPair.getKey(),pointPair.getValue()));
                } );
    }

    private boolean isStraightLine(Point begin, Point end) {
        Integer straightLineTolerance = getConfig().getStraightLineTolerance();
        return  (Math.abs(begin.x - end.x) <= straightLineTolerance) ||
                (Math.abs(begin.y - end.y) <= straightLineTolerance);
    }

    private int computeLineLength(Point begin, Point end) {
        double cat1 = Math.pow(end.x - begin.x + 1,2);
        double cat2 = Math.pow(end.y - begin.y + 1,2);
        return (int)Math.sqrt(cat1 + cat2) + getConfig().getReferenceLinePixelsToAdd();
    }

    private BufferedImage mat2BufferedImage(Mat mat) throws IOException {

        // encode the image...
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", mat, matOfByte);

        // storing the encoded Mat in a byte array...
        byte[] byteArray = matOfByte.toArray();

        // preparing the Buffered Image...
        InputStream in = new ByteArrayInputStream(byteArray);
        return ImageIO.read(in);
    }

    private Rect rectangleToRect(Rectangle rectangle){
        return new Rect(rectangle.x,rectangle.y,rectangle.width, rectangle.height);
    }

    private String cleanTextLine(String textLine) {
        return textLine.trim().replaceAll("\\n","");
    }



}
