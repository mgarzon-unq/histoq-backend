package ar.edu.unq.lom.histoq.backend.service.files;

import ar.edu.unq.lom.histoq.backend.repository.image.exception.ImageFileNotFoundException;
import ar.edu.unq.lom.histoq.backend.service.config.ApplicationConfigProperties;
import ar.edu.unq.lom.histoq.backend.service.files.exception.ImageFileDownloadException;
import ar.edu.unq.lom.histoq.backend.service.files.exception.ImageFileServiceException;
import ar.edu.unq.lom.histoq.backend.service.files.exception.ImageFileUploadException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

@Service
public class ImageFileService implements FileStorageService {

    private final FileSystem fileSystem;
    private final ApplicationConfigProperties applicationConfigProperties;

    ImageFileService(FileSystem fileSystem,
                     ApplicationConfigProperties applicationConfigProperties) {
        this.fileSystem = fileSystem;
        this.applicationConfigProperties = applicationConfigProperties;
    }

    @Override
    public Path getRootFolder() {
        return this.fileSystem.getPath(applicationConfigProperties.getRootFolder());
    }

    @Override
    public String uploadFile(String folderName, MultipartFile file) {
        Path fileDestinationPath = null;
        try {
            fileDestinationPath = getRootFolder().resolve(folderName+"/"+file.getOriginalFilename());
            fileSystem.copyFile(file,fileDestinationPath);
        } catch (FileAlreadyExistsException e) {
            throw new ImageFileUploadException("image.upload-file-already-exist-error",
                    new String[]{file.getOriginalFilename()});
        }
        catch (Exception e) {
            throw new ImageFileUploadException("image.upload-file-error",
                            new String[]{file.getOriginalFilename(),e.getMessage()});
        }
        return fileDestinationPath.toString();
    }

    @Override
    public Resource downloadFile(String folderName, String fileName) {
        try {
            Path file = getRootFolder().resolve(folderName+"/"+fileName);
            Resource resource = fileSystem.getResource(file);

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ImageFileNotFoundException("image.file-not-found-error", new String[]{file.toString()});
            }
        } catch (ImageFileNotFoundException e) {
            throw new ImageFileNotFoundException("image.download-file-error",
                            new String[]{fileName,e.getMessage()});
        } catch (Exception e) {
            throw new ImageFileDownloadException("image.download-file-error",
                            new String[]{fileName,e.getMessage()});
        }
    }

    @Override
    public void deleteFolder(String folderName) {
        fileSystem.deleteFolder(getRootFolder().resolve(folderName));
    }

    @Override
    public void deleteFile(String fileName) {
        fileSystem.deleteFile(getRootFolder().resolve(fileName));
    }

    @Override
    public void createFolder(String folderName) {
        try {
            fileSystem.createFolder(getRootFolder().resolve(folderName));
        } catch (Exception e) {
            throw new ImageFileServiceException("image.folder-error",
                    new String[]{e.getMessage()});
        }
    }
}
