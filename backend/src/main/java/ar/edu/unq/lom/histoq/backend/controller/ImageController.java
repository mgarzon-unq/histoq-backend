package ar.edu.unq.lom.histoq.backend.controller;

import ar.edu.unq.lom.histoq.backend.controller.view.ImageBatchView;
import ar.edu.unq.lom.histoq.backend.model.image.Image;
import ar.edu.unq.lom.histoq.backend.model.image.ImageFile;
import ar.edu.unq.lom.histoq.backend.service.files.FileFormat;
import ar.edu.unq.lom.histoq.backend.service.image.ImageService;
import ar.edu.unq.lom.histoq.backend.service.protocol.ProtocolService;
import ar.edu.unq.lom.histoq.backend.service.securiy.SecurityService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
        return imageService.findAllImageBatches().stream().map(i -> new ImageBatchView(i))
                .collect(Collectors.toList());
    }

    @GetMapping("/image-batches/{batchId}")
    public ImageBatchView findImageBatch(@PathVariable Long batchId) {
        return new ImageBatchView(imageService.findImageBatchById(batchId));
    }

    @GetMapping("/image-batches/image-files/{batchId}")
    public List<ImageFile> findAllImageFiles(@PathVariable Long batchId) {
        return imageService.findAllImageFiles(batchId);
    }

    @GetMapping("/image-batches/processing-default-parameters")
    public Map<String,String> findAllDefaultProcessingParameters() {
        return imageService.findAllDefaultProcessingParameters();
    }

    @GetMapping("/image-batches/image-file/{fileId}")
    @ResponseBody
    public ResponseEntity<Resource> downloadImageFile(@PathVariable Long fileId) {
        Resource file = imageService.downLoadImageFile(fileId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/image-batches/images/{batchId}")
    public List<Image> findAllImages(@PathVariable Long batchId) {
        return imageService.findAllImages(batchId);
    }

    @GetMapping("/image-batches/image/{imageId}")
    @ResponseBody
    public ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) {
        Resource file = imageService.downLoadImage(imageId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping({"/image-batches/image/preview/{fileId}", "/image-batches/image/preview/{fileId}/{rand}"})
    @ResponseBody
    public ResponseEntity<Resource> downloadImagePreview(@PathVariable Long fileId) {
        Resource file = imageService.downLoadImagePreview(fileId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
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
    public List<Image> processImageBatch(@PathVariable Long batchId) {
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
                    "attachment; file=TissueScan-Data." + fileFormat.getExtension() );
        imageService.exportImageBatchesData(imageBatchesIds, fileFormat, response.getWriter());
    }

    @DeleteMapping("/image-batches/{batchId}")
    public void deleteImageBatch(@PathVariable Long batchId) {
        imageService.deleteImageBatchById(batchId);
    }

}
