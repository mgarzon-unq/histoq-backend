package ar.edu.unq.lom.histoq.backend.service.files;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;

public interface FileSystem {
    public Path getPath(String path);
    public void createFolder(Path path) throws IOException;
    public void copyFile(MultipartFile file, Path destinationPath) throws IOException;
    public void getFile(Path path, OutputStream outputStream) throws IOException;
    public void deleteFolder(Path path);
    public void deleteFile(Path path);
}
