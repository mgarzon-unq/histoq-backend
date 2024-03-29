package ar.edu.unq.lom.histoq.backend.service.files;

import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public void getFile(Path path, OutputStream outputStream) throws IOException {
        Files.copy(path, outputStream);
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
