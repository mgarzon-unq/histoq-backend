package ar.edu.unq.lom.histoq.backend.controller;

import ar.edu.unq.lom.histoq.backend.common.InternationalizationException;
import ar.edu.unq.lom.histoq.backend.controller.view.ImageBatchView;
import ar.edu.unq.lom.histoq.backend.model.image.Image;
import ar.edu.unq.lom.histoq.backend.model.image.ImageFile;
import ar.edu.unq.lom.histoq.backend.model.processJob.ProcessJob;
import ar.edu.unq.lom.histoq.backend.service.files.FileFormat;
import ar.edu.unq.lom.histoq.backend.service.files.exception.ImageFileDownloadException;
import ar.edu.unq.lom.histoq.backend.service.image.ImageService;
import ar.edu.unq.lom.histoq.backend.service.protocol.ProtocolService;
import ar.edu.unq.lom.histoq.backend.service.securiy.SecurityService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ImageController {

    private final ImageService imageService;
    private final ProtocolService protocolService;
    private final SecurityService securityService;

    ImageController(ImageService imageService,
                    ProtocolService protocolService,
                    SecurityService securityService) {
        this.imageService = imageService;
        this.protocolService = protocolService;
        this.securityService = securityService;
    }


    @GetMapping("/image-batches")
    public List<ImageBatchView> findAllImageBatches() {
        return imageService.findAllImageBatches().stream().map(ImageBatchView::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/image-batches/{batchId}")
    public ImageBatchView findImageBatch(@PathVariable Long batchId) {
        return new ImageBatchView(imageService.findImageBatchById(batchId));
    }

    @GetMapping("/image-batches/image-files/{batchId}")
    public List<ImageFile> findAllImageFiles(@PathVariable Long batchId) {
        return imageService.findAllUploadedImageFiles(batchId);
    }

    @GetMapping("/image-batches/processing-default-parameters")
    public Map<String,String> findAllDefaultProcessingParameters() {
        return imageService.findAllDefaultProcessingParameters();
    }

    @GetMapping("/image-batches/image-file/{fileId}")
    public void downloadImageFile(@PathVariable Long fileId, HttpServletResponse response) {
        try {
            ImageFile file = imageService.downLoadImageFile(fileId, response.getOutputStream());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
        }
        catch(InternationalizationException e) {
            throw e;
        }
        catch(Exception e) {
            throw new ImageFileDownloadException("image.download-file-error",
                    new String[]{fileId.toString(),
                            e.getMessage()} );
        }
    }

    @GetMapping("/image-batches/images/{batchId}")
    public List<Image> findAllImages(@PathVariable Long batchId) {
        return imageService.findAllImages(batchId);
    }

    @GetMapping("/image-batches/image/{imageId}")
    public void downloadImage(@PathVariable Long imageId, HttpServletResponse response) {
        try {
            Image image = imageService.downLoadImage(imageId, response.getOutputStream());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getName() + "\"");
        }
        catch(InternationalizationException e) {
            throw e;
        }
        catch(Exception e) {
            throw new ImageFileDownloadException("image.download-error",
                    new String[]{e.getMessage()} );
        }
    }

    @GetMapping({"/image-batches/image/preview/{fileId}", "/image-batches/image/preview/{fileId}/{rand}"})
    @ResponseBody
    public void downloadImagePreview(@PathVariable Long fileId, HttpServletResponse response) {
        try {
            Image image = imageService.downLoadImagePreview(fileId, response.getOutputStream());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getName() + "\"");
        }
        catch(InternationalizationException e) {
            throw e;
        }
        catch(Exception e) {
            throw new ImageFileDownloadException("image.download-error",
                    new String[]{e.getMessage()} );
        }
    }

    @PostMapping("/image-batches")
    public ImageBatchView createImageBatch(@RequestBody ImageBatchView imageBatchSummary) {
        return new ImageBatchView(imageService.saveImageBatch(imageBatchSummary.toImageBatch(securityService.getLoggedUser(),protocolService)));
    }

    @PutMapping("/image-batches")
    public ImageBatchView updateImageBatch(@RequestBody ImageBatchView imageBatchView) {
        return new ImageBatchView(imageService.saveImageBatch(imageBatchView.toImageBatch(securityService.getLoggedUser(),protocolService)));
    }

    @PostMapping("/image-batches/image-files/{batchId}")
    public ImageFile uploadImageFile(@RequestBody MultipartFile file, @PathVariable Long batchId) {
        return imageService.uploadImageFile(batchId,file);
    }

    @PutMapping("/image-batches/image-files")
    public ImageFile imageFileUpdate(@RequestBody ImageFile imageFile) {
        return imageService.imageFileUpdate(imageFile);
    }

    @DeleteMapping("/image-batches/image-file/{fileId}")
    public void deleteImageFile(@PathVariable Long fileId) {
        imageService.deleteImageFile(fileId);
    }

    @PostMapping("/image-batches/process/{batchId}")
    public ProcessJob processImageBatch(@PathVariable Long batchId) {
        return imageService.processImageBatch(batchId);
    }

    @GetMapping("/image-batches/data-export/file-formats")
    public List<FileFormat> findImageBatchesSupportedDataExportFileFormats() {
        return imageService.findSupportedDataExportFileFormats();
    }

    @GetMapping("/image-batches/data-export")
    public void imageBatchesDataExport(@RequestParam(name="imageBatchesIds") String pImageBatchesIds,
                                       @RequestParam(name="fileFormat") String pFileFormat,
                                       HttpServletResponse response) throws Exception {
        ObjectMapper om = new ObjectMapper();
        List<Long> imageBatchesIds = om.readValue(pImageBatchesIds,
                new TypeReference<List<Long>>(){});
        FileFormat fileFormat = om.readValue(pFileFormat, FileFormat.class);
        response.setContentType(fileFormat.getContentType());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; file=HistoQ-Data." + fileFormat.getExtension() );
        imageService.exportImageBatchesData(imageBatchesIds, fileFormat, response.getWriter());
    }

    @DeleteMapping("/image-batches/{batchId}")
    public void deleteImageBatch(@PathVariable Long batchId) {
        imageService.deleteImageBatchById(batchId);
    }

}
