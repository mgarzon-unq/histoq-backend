package ar.edu.unq.lom.histoq.backend.service.dataexport;

import ar.edu.unq.lom.histoq.backend.model.image.ImageBatch;

import java.io.PrintWriter;
import java.util.List;

public interface DataExporter {
    public void export(List<ImageBatch> imageBatches, PrintWriter writer);
}
