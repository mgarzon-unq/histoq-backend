package ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.exception.AlgorithmException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class EmbeddedStitchingAlgorithmTests extends EmbeddedAlgorithmTests {

    private final EmbeddedStitchingAlgorithm algorithm = new EmbeddedStitchingAlgorithm();

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void stitchImageFilesTest() {
        List<String> inputImageFiles = Arrays.asList(new String[]{  getFullPath("1378-1.png"),
                getFullPath("1378-2.png"), getFullPath("1378-3.png")});
        String outputFile = getFullPath("stitched-1378.png");

        this.algorithm.stitchImageFiles(inputImageFiles,outputFile);
    }

    @Test
    public void stitchInvalidImageFilesThrowsExceptionTest() {
        assertThrows(AlgorithmException.class, () -> {
            this.algorithm.stitchImageFiles(new ArrayList<String>(),"");
        });
    }
}
