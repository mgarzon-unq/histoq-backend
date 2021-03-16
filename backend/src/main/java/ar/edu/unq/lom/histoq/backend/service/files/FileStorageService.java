package ar.edu.unq.lom.histoq.backend.service.files;

import org.springframework.web.multipart.MultipartFile;
import java.io.OutputStream;
import java.nio.file.Path;


public interface FileStorageService {
    public String uploadFile(String folderName, MultipartFile file);

    public void downloadFile(String fileId, OutputStream outputStream);

    public void deleteFile(String fileId);

    public default boolean isLocal() {return false;}

    public Path getFullPath(String relativePath);
}

