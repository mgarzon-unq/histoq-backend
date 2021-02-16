package ar.edu.unq.lom.histoq.backend.service.dataexport;

import ar.edu.unq.lom.histoq.backend.service.dataexport.exception.DataExportException;
import ar.edu.unq.lom.histoq.backend.model.image.Image;
import ar.edu.unq.lom.histoq.backend.model.image.ImageBatch;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class CSVDataExporter extends BaseDataExporter {

    @Override
    public void export(List<ImageBatch> imageBatches, PrintWriter writer) {
        try {
            CSVPrinter csvPrinter = createPrinter(writer);
            List<String> record = Arrays.asList(new String[21]);

            imageBatches.forEach( batch -> {
                record.set(0,batch.getIndividual().getGroup().getProtocol().getLabel());
                record.set(1,batch.getIndividual().getGroup().getLabel());
                record.set(2,batch.getIndividual().getLabel());
                record.set(3,batch.getDate().toString());
                record.set(4,batch.getId().toString());
                record.set(5,batch.getUser().getFirstName() + " " + batch.getUser().getLastName());

                batch.getImageFiles().forEach( file -> {
                    Image image = file.getImage();
                    if( image == null ) return;

                    Integer imageScaleValue = image.getScaleValue()!=null?image.getScaleValue():0;
                    Integer imageScalePixels = image.getScalePixels()!=null?image.getScalePixels():0;

                    record.set(6,file.getName());
                    record.set(7,image.getName());
                    record.set(8,image.getMeasurementUnit());
                    record.set(9,imageScaleValue.toString());
                    record.set(10,imageScalePixels.toString());
                    record.set(11,image.getMeasurementFactor().toString());
                    record.set(12,image.getTotalArea().toString());
                    record.set(13,image.getTotalTissueArea().toString());
                    record.set(14,image.getViableTissueArea().toString());
                    record.set(15,image.getNecroticTissueArea().toString());
                    record.set(16,Image.pixelsAreaToUnits(image.getTotalArea(),image.getMeasurementFactor()).toString());
                    record.set(17,Image.pixelsAreaToUnits(image.getTotalTissueArea(),image.getMeasurementFactor()).toString());
                    record.set(18,Image.pixelsAreaToUnits(image.getViableTissueArea(),image.getMeasurementFactor()).toString());
                    record.set(19,Image.pixelsAreaToUnits(image.getNecroticTissueArea(),image.getMeasurementFactor()).toString());
                    record.set(20,String.valueOf((image.getNecroticTissueArea()*100.0)/(image.getViableTissueArea()+image.getNecroticTissueArea())));

                    try {
                        csvPrinter.printRecord(record);
                    }
                    catch( Exception e ) {
                        throw new DataExportException("data-export-exception", new String[]{e.getMessage()});
                    }
                });

            });

            csvPrinter.flush();
        }
        catch( Exception e ) {
            throw new DataExportException("data-export-exception", new String[]{e.getMessage()});
        }
    }

    private CSVPrinter  createPrinter(PrintWriter writer) throws IOException {
        return new CSVPrinter(writer,
                CSVFormat.DEFAULT.withHeader(
                        getTranslation("data-export.csv-header-protocol"),
                        getTranslation("data-export.csv-header-experimental-group"),
                        getTranslation("data-export.csv-header-individual"),
                        getTranslation("data-export.csv-header-date-time"),
                        getTranslation("data-export.csv-header-image-batch"),
                        getTranslation("data-export.csv-header-user"),
                        getTranslation("data-export.csv-header-image-file"),
                        getTranslation("data-export.csv-header-image"),
                        getTranslation("data-export.csv-header-measurement-units"),
                        getTranslation("data-export.csv-header-reference-scale-value"),
                        getTranslation("data-export.csv-header-reference-scale-pixels"),
                        getTranslation("data-export.csv-header-conversion-factor"),
                        getTranslation("data-export.csv-header-total-area-pixels"),
                        getTranslation("data-export.csv-header-tissue-area-pixels"),
                        getTranslation("data-export.csv-header-viable-tissue-area-pixels"),
                        getTranslation("data-export.csv-header-necrotic-tissue-area-pixels"),
                        getTranslation("data-export.csv-header-total-area-units"),
                        getTranslation("data-export.csv-header-tissue-area-units"),
                        getTranslation("data-export.csv-header-viable-tissue-area-units"),
                        getTranslation("data-export.csv-header-necrotic-tissue-area-units"),
                        getTranslation("data-export.csv-header-necrotic-tissue-percentage")
                ));
    }

}
