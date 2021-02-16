package ar.edu.unq.lom.histoq.backend.service.files;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;


public interface FileStorageService {
    public Path getRootFolder();

    public String uploadFile(String folderName, MultipartFile file);

    public Resource downloadFile(String folderName, String fileName);

    public void deleteFolder(String folderName);

    public void deleteFile(String fileName);

    public void createFolder(String folderName);
}

