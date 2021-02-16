package ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.exception.AlgorithmException;
import ar.edu.unq.lom.histoq.backend.model.image.algorithms.StitchingAlgorithm;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_stitching.Stitcher;
import java.util.List;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

public class EmbeddedStitchingAlgorithm implements StitchingAlgorithm {

    @Override
    public void stitchImageFiles(List<String> filePaths, String outputFilePath) throws AlgorithmException {
        MatVector imagesToStitch = new MatVector();
        org.bytedeco.opencv.opencv_core.Mat outputImage = new org.bytedeco.opencv.opencv_core.Mat();
        Stitcher stitcher = Stitcher.create(Stitcher.SCANS);

        filePaths.forEach( path -> imagesToStitch.push_back(imread(path)));

        Integer status = stitcher.stitch(imagesToStitch, outputImage);
        if( status != Stitcher.OK )
            throw new AlgorithmException( stitcherErrorCodeToMessageId(status),
                    new String[]{String.valueOf(status)});

        imwrite(outputFilePath,outputImage);
    }

    private String stitcherErrorCodeToMessageId(Integer errorCode) {
        switch(errorCode) {
            case Stitcher.ERR_NEED_MORE_IMGS:
                return "embedded.stitching.algorithm-error-need-more-images";
            case Stitcher.ERR_HOMOGRAPHY_EST_FAIL:
                return "embedded.stitching.algorithm-error-homography-estimation-fail";
            case Stitcher.ERR_CAMERA_PARAMS_ADJUST_FAIL:
                return "embedded.stitching.algorithm-error-camera-params-adjust-fail";
        }
        return "embedded.stitching.algorithm-error";
    }
}
