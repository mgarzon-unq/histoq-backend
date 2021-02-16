package ar.edu.unq.lom.histoq.backend.service.image;

import ar.edu.unq.lom.histoq.backend.model.image.*;
import ar.edu.unq.lom.histoq.backend.repository.image.ImageBatchRepository;
import ar.edu.unq.lom.histoq.backend.repository.image.ImageFileRepository;
import ar.edu.unq.lom.histoq.backend.repository.image.ImageRepository;
import ar.edu.unq.lom.histoq.backend.repository.image.exception.ImageBatchNotFoundException;
import ar.edu.unq.lom.histoq.backend.repository.image.exception.ImageFileNotFoundException;
import ar.edu.unq.lom.histoq.backend.repository.image.exception.ImageNotFoundException;
import ar.edu.unq.lom.histoq.backend.service.context.TissueScanAppContext;
import ar.edu.unq.lom.histoq.backend.service.dataexport.DataExporter;
import ar.edu.unq.lom.histoq.backend.service.dataexport.DataExporterFactory;
import ar.edu.unq.lom.histoq.backend.service.securiy.BaseServiceWithSecurity;
import ar.edu.unq.lom.histoq.backend.service.securiy.SecurityService;
import ar.edu.unq.lom.histoq.backend.service.files.FileFormat;
import ar.edu.unq.lom.histoq.backend.service.files.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.transaction.Transactional;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ImageService extends BaseServiceWithSecurity {

    private final FileStorageService imageFileService;
    private final ImageBatchRepository imageBatchRepository;
    private final ImageRepository imageRepository;
    private final ImageFileRepository imageFileRepository;
    private ImageScanner imageScanner;

    ImageService(FileStorageService imageFileService,
                 ImageBatchRepository imageBatchRepository,
                 ImageFileRepository imageFileRepository,
                 ImageRepository imageRepository,
                 SecurityService securityService) {
        super(securityService);
        this.imageFileService = imageFileService;
        this.imageBatchRepository = imageBatchRepository;
        this.imageFileRepository = imageFileRepository;
        this.imageRepository = imageRepository;
    }

    public ImageBatch findImageBatchById(Long id) {
        userAccessControl(null);
        return this.imageBatchRepository
                .findById(id)
                .orElseThrow(() -> new ImageBatchNotFoundException("repository.image-batch-not-found",
                                new String[]{id.toString()}));
    }

    public ImageFile findImageFileById(Long id) {
        userAccessControl(null);
        return this.imageFileRepository
                .findById(id)
                .orElseThrow(() -> new ImageFileNotFoundException("repository.image-file-not-found",
                        new String[]{id.toString()}));
    }

    public List<ImageBatch> findAllImageBatches() {
        userAccessControl(null);

        if( getLoggedUser().isAdmin() )
            return imageBatchRepository.findAll();
        return imageBatchRepository.findAllByUserId(getLoggedUser().getId());
    }

    public List<ImageFile> findAllImageFiles(Long batchId) {
        userAccessControl(null);
        return findImageBatchById(batchId)
                .getImageFiles();
    }

    public List<Image> findAllImages(Long batchId) {
        userAccessControl(null);
        return findAllImageFiles(batchId)
                .stream()
                .map(file -> file.getImage())
                .collect(Collectors.toSet())
                .stream()
                .filter( image -> image != null )
                .collect(Collectors.toList());
    }

    @Transactional
    public ImageBatch saveImageBatch(ImageBatch imageBatch) {
        userAccessControl(null);
        ImageBatch newBatch = this.imageBatchRepository.save(imageBatch);
        imageFileService.createFolder(newBatch.getId().toString());
        return newBatch;
    }

    @Transactional
    public ImageFile uploadImageFile(Long batchId, MultipartFile file) {
        userAccessControl(null);
        ImageBatch imageBatch = findImageBatchById(batchId);

        // upload file...
        String localPath = this.imageFileService.uploadFile(batchId.toString(), file);

        ImageFile imageFile = new ImageFile();
        imageFile.setBatch(imageBatch);
        imageFile.setName(file.getOriginalFilename());

        imageBatch.getImageFiles().add(imageFile);

        this.imageBatchRepository.save(imageBatch);

        return imageFile;
    }

    public Resource downLoadImageFile(Long imageFileId) {
        userAccessControl(null);
        ImageFile imageFile = findImageFileById(imageFileId);
        String batchFolder  = imageFile.getBatch().getId().toString();
        return this.imageFileService.downloadFile( batchFolder, imageFile.getName() );
    }

    public Resource downLoadImage(Long imageId) {
        userAccessControl(null);
        Image image = this.imageRepository.findById(imageId)
                        .orElseThrow(() -> new ImageNotFoundException("repository.image-not-found",
                                        new String[]{imageId.toString()})
                        );
        String batchFolder = image.getFiles().get(0).getBatch().getId().toString();
        return this.imageFileService.downloadFile( batchFolder, image.getName() );
    }

    @Transactional
    public List<Image> processImageBatch(Long batchId) {
        userAccessControl(null);
        ImageBatch imageBatch = findImageBatchById(batchId);
        imageBatch.process(getImageScanner());
        this.imageBatchRepository.save(imageBatch);
        return findAllImages(batchId);
    }

    public List<FileFormat> findSupportedDataExportFileFormats() {
        userAccessControl(null);
        return DataExporterFactory.getSupportedDataExportFileFormats();
    }

    public void exportImageBatchesData(List<Long> imageBatchesIds, FileFormat format, PrintWriter writer) {        ;
        userAccessControl(null);
        DataExporter exporter = DataExporterFactory.getDataExporter(format);
        exporter.export(this.imageBatchRepository.findAllById(imageBatchesIds),writer);
    }

    @Transactional
    public void deleteImageBatchById(Long batchId) {
        userAccessControl(null);
        ImageBatch imageBatch = findImageBatchById(batchId);
        this.imageFileService.deleteFolder(batchId.toString());
        this.imageBatchRepository.deleteById(batchId);
    }

    @Transactional
    public void deleteImageFile(Long imageFileId) {
        userAccessControl(null);
        ImageFile imageFile = findImageFileById(imageFileId);
        String batchFolder  = imageFile.getBatch().getId().toString();
        this.imageFileService.deleteFile(batchFolder + "/" + imageFile.getName() );
        this.imageFileRepository.deleteById(imageFileId);
    }

    @Transactional
    public ImageFile imageFileUpdate(ImageFile imageFileChanges) {
        userAccessControl(null);
        ImageFile imageFile = findImageFileById(imageFileChanges.getId());
        // only customized parameters changes are allowed...
        imageFile.setCustomProcessingParameters(imageFileChanges.getCustomProcessingParameters());
        return this.imageFileRepository.save(imageFile);
    }

    public Resource downLoadImagePreview(Long fileId) {
        userAccessControl(null);
        ImageFile imageFile = findImageFileById(fileId);
        Image imagePreview = getImageScanner().previewProcessedImageFile(imageFile);
        String batchFolder = imageFile.getBatch().getId().toString();
        return this.imageFileService.downloadFile( batchFolder, imagePreview.getName() );
    }

    public Map<String,String> findAllDefaultProcessingParameters() {
        userAccessControl(null);
        return getImageScanner().getDefaultProcessingParameters();
    }

    private ImageScanner getImageScanner() {
        if( this.imageScanner == null ) this.imageScanner = TissueScanAppContext.getBean(TissueScanner.class);
        return this.imageScanner;
    }

}
