package ar.edu.unq.lom.histoq.backend.service.image;

import ar.edu.unq.lom.histoq.backend.model.image.*;
import ar.edu.unq.lom.histoq.backend.repository.image.ImageBatchRepository;
import ar.edu.unq.lom.histoq.backend.repository.image.ImageFileRepository;
import ar.edu.unq.lom.histoq.backend.repository.image.ImageRepository;
import ar.edu.unq.lom.histoq.backend.repository.image.exception.ImageBatchNotFoundException;
import ar.edu.unq.lom.histoq.backend.repository.image.exception.ImageFileNotFoundException;
import ar.edu.unq.lom.histoq.backend.repository.image.exception.ImageNotFoundException;
import ar.edu.unq.lom.histoq.backend.service.context.HistoQAppContext;
import ar.edu.unq.lom.histoq.backend.service.dataexport.DataExporter;
import ar.edu.unq.lom.histoq.backend.service.dataexport.DataExporterFactory;
import ar.edu.unq.lom.histoq.backend.service.files.FileStorageInfo;
import ar.edu.unq.lom.histoq.backend.service.files.FileStorageServiceManager;
import ar.edu.unq.lom.histoq.backend.service.securiy.BaseServiceWithSecurity;
import ar.edu.unq.lom.histoq.backend.service.securiy.SecurityService;
import ar.edu.unq.lom.histoq.backend.service.files.FileFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.transaction.Transactional;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ImageService extends BaseServiceWithSecurity {

    private final FileStorageServiceManager fileStorageServiceManager;
    private final ImageBatchRepository imageBatchRepository;
    private final ImageRepository imageRepository;
    private final ImageFileRepository imageFileRepository;
    private ImageScanner imageScanner;

    ImageService(FileStorageServiceManager fileStorageServiceManager,
                 ImageBatchRepository imageBatchRepository,
                 ImageFileRepository imageFileRepository,
                 ImageRepository imageRepository,
                 SecurityService securityService) {
        super(securityService);
        this.fileStorageServiceManager = fileStorageServiceManager;
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

    public List<ImageFile> findAllUploadedImageFiles(Long batchId) {
        userAccessControl(null);
        return findImageBatchById(batchId)
                .getUploadedImageFiles();
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
        return newBatch;
    }

    @Transactional
    public ImageFile uploadImageFile(Long batchId, MultipartFile file) {
        userAccessControl(null);
        ImageBatch imageBatch = findImageBatchById(batchId);

        ImageFile imageFile = new ImageFile();
        imageFile.setBatch(imageBatch);
        imageFile.setName(file.getOriginalFilename());
        FileStorageInfo storageInfo = this.fileStorageServiceManager.uploadFile(batchId.toString(), file);
        imageFile.setStorage(storageInfo.getStorage());
        imageFile.setIdInStorage(storageInfo.getIdInStorage());

        imageBatch.addImageFile(imageFile);

        this.imageBatchRepository.save(imageBatch);

        return imageFile;
    }

    public ImageFile downLoadImageFile(Long imageFileId, OutputStream outputStream) {
        userAccessControl(null);
        ImageFile imageFile = findImageFileById(imageFileId);
        this.fileStorageServiceManager.downloadFile(
                imageFile.getStorage(),
                imageFile.getIdInStorage(),
                outputStream );
        return imageFile;
    }

    public Image downLoadImage(Long imageId, OutputStream outputStream) {
        userAccessControl(null);
        Image image = this.imageRepository.findById(imageId)
                        .orElseThrow(() -> new ImageNotFoundException("repository.image-not-found",
                                        new String[]{imageId.toString()})
                        );
        ImageFile imageFile = image.getMainImageFile();
        this.fileStorageServiceManager.downloadFile(
                imageFile.getStorage(),
                imageFile.getIdInStorage(),
                outputStream );
        return image;
    }

    @Transactional
    public List<Image> processImageBatch(Long batchId) {
        userAccessControl(null);

        ImageBatch imageBatch = findImageBatchById(batchId);

        removeImageBatchGeneratedFilesInStorage(imageBatch);

        copyImageBatchFilesToLocalFileStorage(imageBatch);

        imageBatch.process(getImageScanner());

        sendToFileStorageImageBatchGeneratedFile(imageBatch);

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
        removeImageBatchAllFilesInStorage(imageBatch);
        this.imageBatchRepository.deleteById(batchId);
    }

    @Transactional
    public void deleteImageFile(Long imageFileId) {
        userAccessControl(null);
        ImageFile imageFile = findImageFileById(imageFileId);
        removeImageFileInStorage(imageFile);
        this.imageFileRepository.deleteById(imageFileId);
    }

    @Transactional
    public ImageFile imageFileUpdate(ImageFile imageFileChanges) {
        userAccessControl(null);
        ImageFile imageFile = findImageFileById(imageFileChanges.getId());
        // only customized parameters changes are allowed...
        imageFile.updateCustomProcessingParameters(imageFileChanges.getCustomProcessingParameters());
        return this.imageFileRepository.save(imageFile);
    }

    public Image downLoadImagePreview(Long fileId, OutputStream outputStream) {
        userAccessControl(null);

        ImageFile imageFile = findImageFileById(fileId);

        copyImageFileToLocalFileStorage(imageFile);

        Image imagePreview = getImageScanner().previewProcessedImageFile(imageFile);

        this.fileStorageServiceManager.downloadLocalFile(
                imageFile.getBatch().getId().toString()+"/"+imagePreview.getName(),
                outputStream );

        return imagePreview;
    }

    public Map<String,String> findAllDefaultProcessingParameters() {
        userAccessControl(null);
        return getImageScanner().getDefaultProcessingParameters();
    }

    private ImageScanner getImageScanner() {
        if( this.imageScanner == null ) this.imageScanner = HistoQAppContext.getBean(TissueScanner.class);
        return this.imageScanner;
    }

    private void removeImageBatchAllFilesInStorage(ImageBatch imageBath) {
        imageBath.getImageFiles().forEach( imageFile -> {
            removeImageFileInStorage(imageFile);
        });
    }

    private void removeImageBatchGeneratedFilesInStorage(ImageBatch imageBath) {
        removeImageBatchAnyFilesInStorage(imageBath,ImageFile::isGenerated);
    }

    private void removeImageBatchAnyFilesInStorage(ImageBatch imageBath, Predicate<ImageFile> predicate) {
        imageBath.getImageFiles().stream().filter(predicate).forEach( imageFile -> {
            removeImageFileInStorage(imageFile);
        });
    }

    private void removeImageFileInStorage(ImageFile imageFile) {
        this.fileStorageServiceManager.deleteFile(
                imageFile.getStorage(),
                imageFile.getIdInStorage() );
    }

    private void copyImageBatchFilesToLocalFileStorage(ImageBatch imageBath) {
        for (ImageFile imageFile : imageBath.getImageFiles()) {
            copyImageFileToLocalFileStorage(imageFile);
        }
    }

    private void copyImageFileToLocalFileStorage(ImageFile imageFile) {
        try {
            this.fileStorageServiceManager.copyFileToLocalStorage(
                    imageFile.getStorage(),
                    imageFile.getIdInStorage(),
                    imageFile.getBatch().getId().toString(),
                    imageFile.getName() );
        } catch (Exception e) {
            throw new ImageNotFoundException("image.file-processing-error",
                    new String[]{imageFile.getName(),e.getMessage()});
        }
    }

    private void sendToFileStorageImageBatchGeneratedFile(ImageBatch imageBatch) {
        imageBatch.getImageFiles()
                .stream()
                .filter(ImageFile::isGenerated)
                .forEach(file -> {
                    String folderName = imageBatch.getId().toString();
                    FileStorageInfo storageInfo = this.fileStorageServiceManager
                            .copyLocalFileToRemoteStorage(folderName,file.getName());
                    file.setStorage(storageInfo.getStorage());
                    file.setIdInStorage(storageInfo.getIdInStorage());
                });
    }


}
