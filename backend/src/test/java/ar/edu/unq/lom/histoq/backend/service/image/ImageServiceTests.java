package ar.edu.unq.lom.histoq.backend.service.image;

import ar.edu.unq.lom.histoq.backend.model.image.Image;
import ar.edu.unq.lom.histoq.backend.model.image.ImageBatch;
import ar.edu.unq.lom.histoq.backend.model.image.ImageFile;
import ar.edu.unq.lom.histoq.backend.repository.image.ImageBatchRepository;
import ar.edu.unq.lom.histoq.backend.repository.image.ImageFileRepository;
import ar.edu.unq.lom.histoq.backend.repository.image.ImageRepository;
import ar.edu.unq.lom.histoq.backend.service.files.FileFormat;
import ar.edu.unq.lom.histoq.backend.service.files.FileStorageInfo;
import ar.edu.unq.lom.histoq.backend.service.files.FileStorageServiceManager;
import ar.edu.unq.lom.histoq.backend.service.securiy.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ImageServiceTests {

    private ImageService imageService ;

    @Mock
    private FileStorageInfo storageInfo;
    @Mock
    private FileStorageServiceManager fileStorageServiceManager;
    @Mock
    private ImageBatchRepository imageBatchRepository;
    @Mock
    private ImageFileRepository imageFileRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private SecurityService securityService;
    @Mock
    ImageBatch imageBatch;
    private Long batchId = 1l;

    @BeforeEach
    public void setUp() {
        this.imageService = new ImageService(this.fileStorageServiceManager,
                this.imageBatchRepository,
                this.imageFileRepository,
                this.imageRepository,
                this.securityService);

        when(this.imageBatchRepository.findById(this.batchId)).thenReturn(Optional.of(this.imageBatch));
    }

    @Test
    public void processImageBatchTest() {
        ImageFile imageFile = mock(ImageFile.class);
        Image image = mock(Image.class);
        List<ImageFile> imageFiles = Arrays.asList(new ImageFile[]{imageFile});

        when(this.imageBatch.getImageFiles()).thenReturn(imageFiles);
        when(imageFile.getImage()).thenReturn(image);

        List<Image> images = this.imageService.processImageBatch(batchId);

        verify(this.imageBatch,times(1)).process(any());
        verify(this.imageBatchRepository,times(1)).save(this.imageBatch);

        assertEquals( image, images.get(0));
    }

    @Test
    public void uploadImageFileTest() {
        MultipartFile file = mock(MultipartFile.class);
        String fileName = "image.png";
        List<ImageFile> imageFiles = new ArrayList<>();

        when(file.getOriginalFilename()).thenReturn(fileName);
        when(this.fileStorageServiceManager.uploadFile(this.batchId.toString(),file)).thenReturn(this.storageInfo);
        when(this.imageBatch.getImageFiles()).thenReturn(imageFiles);

        ImageFile imageFile = this.imageService.uploadImageFile(this.batchId,file);

        assertEquals(fileName, imageFile.getName());
        assertEquals(this.imageBatch,imageFile.getBatch());
        assertEquals(imageFile,imageFiles.get(0));

        verify(this.fileStorageServiceManager,times(1)).uploadFile(this.batchId.toString(),file);
        verify(this.imageBatchRepository,times(1)).save(this.imageBatch);
    }

    @Test
    public void exportImageBatchesDataTest() {
        List<Long> batchesIds = Arrays.asList(new Long[]{1l,2l,3l});
        FileFormat fileFormat = mock(FileFormat.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(fileFormat.getId()).thenReturn(1);
        when(this.imageBatchRepository.findAllById(anyList())).thenReturn(new ArrayList<>());

        this.imageService.exportImageBatchesData(batchesIds,fileFormat,writer);

        verify(this.imageBatchRepository,times(1)).findAllById(batchesIds);
    }
}
