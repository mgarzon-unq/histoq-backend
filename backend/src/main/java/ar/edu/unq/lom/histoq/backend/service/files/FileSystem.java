package ar.edu.unq.lom.histoq.backend.service.files;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

public interface FileSystem {
    public Path getPath(String path);
    public void createFolder(Path path) throws IOException;
    public void copyFile(MultipartFile file, Path destinationPath) throws IOException;
    public Resource getResource(Path path) throws MalformedURLException;
    public void deleteFolder(Path path);
    public void deleteFile(Path path);
}
