package ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.TissueData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EmbeddedTissueAnalysisAlgorithmTests extends EmbeddedAlgorithmTests {

    private final EmbeddedTissueAnalysisAlgorithm algorithm = new EmbeddedTissueAnalysisAlgorithm();

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void findTissueAreasTest() {

        TissueData tissueData = this.algorithm.findTissueAreas(getFullPath("1343.png"),getFullPath("scanned-1343.png"));

        assertEquals(751224.0,tissueData.getTotalArea());
        assertEquals(241524.0,tissueData.getTotalTissueArea());
        assertEquals(108988.0,tissueData.getViableTissueArea());
        assertEquals(73452.5,tissueData.getNecroticTissueArea());
    }

}
