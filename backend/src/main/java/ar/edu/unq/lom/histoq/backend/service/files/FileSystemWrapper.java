package ar.edu.unq.lom.histoq.backend.service.files;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileSystemWrapper implements FileSystem {

    @Override
    public Path getPath(String path) {
        return Paths.get(path);
    }

    @Override
    public void createFolder(Path path) throws IOException {
        Files.createDirectory(path);
    }

    @Override
    public void copyFile(MultipartFile file, Path destinationPath) throws IOException {
        Files.copy(file.getInputStream(), destinationPath);
    }

    @Override
    public Resource getResource(Path path) throws MalformedURLException {
        return new UrlResource(path.toUri());
    }

    @Override
    public void deleteFolder(Path path) {
        FileSystemUtils.deleteRecursively(path.toFile());
    }

    @Override
    public void deleteFile(Path path) {
        FileSystemUtils.deleteRecursively(path.toFile());
    }
}
