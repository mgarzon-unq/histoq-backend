package ar.edu.unq.lom.histoq.backend.service.files;

import ar.edu.unq.lom.histoq.backend.repository.image.exception.ImageFileNotFoundException;
import ar.edu.unq.lom.histoq.backend.service.config.ApplicationConfigProperties;
import ar.edu.unq.lom.histoq.backend.service.files.exception.ImageFileDownloadException;
import ar.edu.unq.lom.histoq.backend.service.files.exception.ImageFileServiceException;
import ar.edu.unq.lom.histoq.backend.service.files.exception.ImageFileUploadException;
import org.springframework.web.multipart.MultipartFile;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LocalFileStorageService implements FileStorageService {

    private final FileSystem fileSystem;
    private final ApplicationConfigProperties applicationConfigProperties;

    LocalFileStorageService(FileSystem fileSystem,
                            ApplicationConfigProperties applicationConfigProperties) {
        this.fileSystem = fileSystem;
        this.applicationConfigProperties = applicationConfigProperties;
    }

    @Override
    public boolean isLocal() {return true;}

    @Override
    public Path getFullPath(String relativePath) {
        return getRootFolder().resolve(relativePath);
    }

    @Override
    public String uploadFile(String folderName, MultipartFile file) {
        try {
            String relativePath = folderName + "/" + file.getOriginalFilename();

            if( !Files.exists(getFullPath(folderName)) )
                createFolder(folderName);

            fileSystem.copyFile( file, getFullPath(relativePath));

            return relativePath;
        }
        catch (FileAlreadyExistsException e) {
            throw new ImageFileUploadException("image.upload-file-already-exist-error",
                    new String[]{file.getOriginalFilename()});
        }
        catch (Exception e) {
            throw new ImageFileUploadException("image.upload-file-error",
                            new String[]{file.getOriginalFilename(),e.getMessage()});
        }
    }

    @Override
    public void downloadFile(String fileId, OutputStream outputStream) {
        try {
            fileSystem.getFile(getFullPath(fileId), outputStream);
        } catch (ImageFileNotFoundException e) {
            throw new ImageFileNotFoundException("image.download-file-error",
                            new String[]{fileId,e.getMessage()});
        } catch (Exception e) {
            throw new ImageFileDownloadException("image.download-file-error",
                            new String[]{fileId,e.getMessage()});
        }
    }

    @Override
    public void deleteFile(String fileId) {
        fileSystem.deleteFile(getFullPath(fileId));
    }

    private void createFolder(String folderName) {
        try {
            fileSystem.createFolder(getFullPath(folderName));
        } catch (Exception e) {
            throw new ImageFileServiceException("image.folder-error",
                    new String[]{e.getMessage()});
        }
    }

    private Path getRootFolder() {
        return this.fileSystem.getPath(applicationConfigProperties.getRootFolder());
    }
}
