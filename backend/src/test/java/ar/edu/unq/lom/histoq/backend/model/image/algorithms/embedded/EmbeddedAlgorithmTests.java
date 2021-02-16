package ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded;

import java.nio.file.Paths;

public class EmbeddedAlgorithmTests {
    private final String rootFolder = "./src/test/resources/images/";

    protected String getFullPath(String fileName) {
        return Paths.get(this.rootFolder + fileName).toString();
    }
}
