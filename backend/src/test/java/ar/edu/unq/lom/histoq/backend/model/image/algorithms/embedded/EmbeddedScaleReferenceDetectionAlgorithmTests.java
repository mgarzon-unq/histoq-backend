package ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EmbeddedScaleReferenceDetectionAlgorithmTests extends EmbeddedAlgorithmTests {

    private final EmbeddedScaleReferenceDetectionAlgorithm algorithm = new EmbeddedScaleReferenceDetectionAlgorithm();

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void findScaleReferenceTest() {

        /*ImageScaleReference imageScaleReference = this.algorithm.findScaleReference(getFullPath("1343.png"));

        assertEquals(1000,imageScaleReference.getScaleValue());
        assertEquals( "um", imageScaleReference.getMeasurementUnit());
        assertEquals( 131, imageScaleReference.getScalePixels());*/
    }

}
