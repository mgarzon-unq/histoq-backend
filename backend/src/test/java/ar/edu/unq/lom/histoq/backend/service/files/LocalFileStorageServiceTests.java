package ar.edu.unq.lom.histoq.backend.service.files;


import ar.edu.unq.lom.histoq.backend.model.storage.FileStorage;
import ar.edu.unq.lom.histoq.backend.service.config.ApplicationConfigProperties;
import ar.edu.unq.lom.histoq.backend.service.files.exception.ImageFileUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
public class LocalFileStorageServiceTests {

    private LocalFileStorageService localFileStorageService;

    @Mock
    private FileSystem fileSystem;
    @Mock
    private ApplicationConfigProperties applicationConfigProperties;
    private final Path rootFolder = Paths.get("./");

    @BeforeEach
    public void setUp() {
        this.localFileStorageService = new LocalFileStorageService(
                                                        this.fileSystem,
                                                        this.applicationConfigProperties);
        when(this.applicationConfigProperties.getRootFolder()).thenReturn(this.rootFolder.toString());
        when(this.fileSystem.getPath(anyString())).thenReturn(this.rootFolder);
    }
    
    @Test
    void uploadNotFoundFileThrowsExceptionTest() {
        assertThrows(ImageFileUploadException.class, () -> {
            MultipartFile file = mock(MultipartFile.class);

            when(file.getOriginalFilename()).thenReturn("");
            Mockito.doThrow(ImageFileUploadException.class).when(this.fileSystem).copyFile(any(),any());

            this.localFileStorageService.uploadFile("",file);
        });
    }

    @Test
    void uploadTest() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String fileName = "test.txt";
        String batchFolder = "1";
        String expectedFilePath = this.rootFolder.resolve( batchFolder + "/" + fileName).toString();

        when(file.getOriginalFilename()).thenReturn(fileName);

        this.localFileStorageService.uploadFile(batchFolder,file);
    }
}
