package ar.edu.unq.lom.histoq.backend.controller;

import ar.edu.unq.lom.histoq.backend.controller.view.ImageBatchView;
import ar.edu.unq.lom.histoq.backend.model.image.Image;
import ar.edu.unq.lom.histoq.backend.model.image.ImageBatch;
import ar.edu.unq.lom.histoq.backend.model.image.ImageFile;
import ar.edu.unq.lom.histoq.backend.model.protocol.ExperimentalGroup;
import ar.edu.unq.lom.histoq.backend.model.protocol.Individual;
import ar.edu.unq.lom.histoq.backend.model.protocol.Protocol;
import ar.edu.unq.lom.histoq.backend.model.user.User;
import ar.edu.unq.lom.histoq.backend.service.files.FileFormat;
import ar.edu.unq.lom.histoq.backend.service.image.ImageService;
import ar.edu.unq.lom.histoq.backend.service.protocol.ProtocolService;
import ar.edu.unq.lom.histoq.backend.service.securiy.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ImageControllerTests {

    private ImageController imageController;

    @Mock
    private ImageService imageService;
    @Mock
    private ProtocolService protocolService;
    @Mock
    private SecurityService securityService;
    @Mock
    private User loggedUser;
    @Mock
    private ImageBatch batch;
    @Mock
    private Individual individual;
    @Mock
    private ExperimentalGroup experimentalGroup;
    @Mock
    private Protocol protocol;

    @BeforeEach
    public void setUp() {
        this.imageController = new ImageController( this.imageService,
                                                    this.protocolService,
                                                    this.securityService );

        when(this.batch.getId()).thenReturn(1l);
        when(this.batch.getDate()).thenReturn(new Date());
        when(this.batch.isApplyStitching()).thenReturn(false);
        when(this.batch.getIndividual()).thenReturn(this.individual);
        when(this.batch.getUser()).thenReturn(loggedUser);
        when(this.individual.getId()).thenReturn(1l);
        when(this.individual.getLabel()).thenReturn("");
        when(this.individual.getGroup()).thenReturn(this.experimentalGroup);
        when(this.experimentalGroup.getProtocol()).thenReturn(this.protocol);
        when(this.protocol.getLabel()).thenReturn("");
        when(this.protocol.getTitle()).thenReturn("");
        when(this.loggedUser.getEmail()).thenReturn("");
        when(this.loggedUser.getFirstName()).thenReturn("");
        when(this.loggedUser.getLastName()).thenReturn("");

        when(this.securityService.getLoggedUser()).thenReturn(this.loggedUser);
        when(this.imageService.saveImageBatch(this.batch)).thenReturn(this.batch);
    }

    @Test
    void findAllImageBatchesTest() {
        when(this.imageService.findAllImageBatches()).thenReturn(new ArrayList<>());

        this.imageController.findAllImageBatches();

        verify(this.imageService,times(1)).findAllImageBatches();
    }

    @Test
    void findImageBatchTest() {
        Long batchId = 1l;

        when(this.imageService.findImageBatchById(batchId)).thenReturn(this.batch);

        this.imageController.findImageBatch(batchId);

        verify(this.imageService,times(1)).findImageBatchById(batchId);
    }

    @Test
    void findAllImageFilesTest() {
        Long batchId = 1l;

        when(this.imageService.findAllImageFiles(batchId)).thenReturn(anyList());

        this.imageController.findAllImageFiles(batchId);

        verify(this.imageService,times(1)).findAllImageFiles(batchId);
    }

    @Test
    void findAllDefaultProcessingParametersTest() {
        when(this.imageService.findAllDefaultProcessingParameters()).thenReturn(new HashMap<>());

        this.imageController.findAllDefaultProcessingParameters();

        verify(this.imageService,times(1)).findAllDefaultProcessingParameters();
    }

    @Test
    void downloadImageFileTest() {
        Long fileId = 1l;
        Resource file = mock(Resource.class);

        when(this.imageService.downLoadImageFile(fileId)).thenReturn(file);
        this.imageController.downloadImageFile(fileId);

        verify(this.imageService,times(1)).downLoadImageFile(fileId);
    }

    @Test
    void findAllImagesTest() {
        Long batchId = 1l;

        when(this.imageService.findAllImages(batchId)).thenReturn(anyList());

        this.imageController.findAllImages(batchId);

        verify(this.imageService,times(1)).findAllImages(batchId);
    }

    @Test
    void downloadImageTest() {
        Long imageId = 1l;
        Resource file = mock(Resource.class);

        when(this.imageService.downLoadImage(imageId)).thenReturn(file);
        this.imageController.downloadImage(imageId);

        verify(this.imageService,times(1)).downLoadImage(imageId);
    }

    @Test
    void downloadImagePreviewTest() {
        Long fileId = 1l;
        Resource file = mock(Resource.class);

        when(this.imageService.downLoadImagePreview(fileId)).thenReturn(file);
        this.imageController.downloadImagePreview(fileId);

        verify(this.imageService,times(1)).downLoadImagePreview(fileId);
    }

    @Test
    void createImageBatchTest() {
        ImageBatchView imageBatchView = mock(ImageBatchView.class);

        when(imageBatchView.toImageBatch(this.loggedUser,this.protocolService)).thenReturn(this.batch);

        this.imageController.createImageBatch(imageBatchView);

        verify(this.imageService,times(1)).saveImageBatch(this.batch);
    }

    @Test
    void uploadImageFileTest() {
        MultipartFile file = mock(MultipartFile.class);
        ImageFile imageFile = mock(ImageFile.class);
        Long batchId = 1l;

        when(this.imageService.uploadImageFile(any(),any())).thenReturn(imageFile);

        Assertions.assertEquals(imageFile, this.imageController.uploadImageFile(file,batchId));

        verify(this.imageService,times(1)).uploadImageFile(batchId,file);
    }

    @Test
    void imageFileUpdateTest() {
        ImageFile imageFile = mock(ImageFile.class);

        this.imageController.imageFileUpdate(imageFile);

        verify(this.imageService,times(1)).imageFileUpdate(imageFile);
    }

    @Test
    void deleteImageFileTest() {
        Long fileId = 1l;

        this.imageController.deleteImageFile(fileId);

        verify(this.imageService,times(1)).deleteImageFile(fileId);
    }

    @Test
    void processImageBatchTest() {
        Long batchId = 1l;
        List<Image> images = mock(List.class);

        when(this.imageService.processImageBatch(anyLong())).thenReturn(images);

        assertEquals(images, this.imageController.processImageBatch(batchId) );

        verify(this.imageService,times(1)).processImageBatch(batchId);
    }

    @Test
    void findImageBatchesSupportedDataExportFileFormatsTest() {
        this.imageController.findImageBatchesSupportedDataExportFileFormats();

        verify(this.imageService,times(1)).findSupportedDataExportFileFormats();
    }

    @Test
    void imageBatchesDataExportTest() {
        List<Long> batchesIds = Arrays.asList(new Long[]{1l,2l,3l});
        FileFormat fileFormat = new FileFormat(1,"text/csv","csv","csv");
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        ObjectMapper mapper = new ObjectMapper();

        try {
            when(response.getWriter()).thenReturn(writer);

            this.imageController.imageBatchesDataExport(mapper.writeValueAsString(batchesIds),
                    mapper.writeValueAsString(fileFormat),
                    response);

            verify(this.imageService,times(1)).exportImageBatchesData(anyList(),any(FileFormat.class),any());
        }
        catch( Exception e ) {
            assert(false);
        }
    }
}
