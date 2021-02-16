package ar.edu.unq.lom.histoq.backend.model.image;

import java.util.List;
import java.util.Map;

public interface ImageScanner {
    public Image processImageFile(ImageFile imageFile);
    public ImageFile stitchImageFiles(List<ImageFile> imageFiles, String fileGroupName);
    public Map<String,String> getDefaultProcessingParameters();
    public Image previewProcessedImageFile(ImageFile imageFile);
}
