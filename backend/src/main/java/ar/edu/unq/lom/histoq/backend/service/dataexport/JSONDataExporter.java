package ar.edu.unq.lom.histoq.backend.service.dataexport;

import ar.edu.unq.lom.histoq.backend.service.dataexport.exception.DataExportException;
import ar.edu.unq.lom.histoq.backend.model.image.Image;
import ar.edu.unq.lom.histoq.backend.model.image.ImageBatch;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class JSONDataExporter extends BaseDataExporter {

    @Override
    public void export(List<ImageBatch> imageBatches, PrintWriter writer) {
        try {
            List<ImageBatchDataExport> batches = new ArrayList<>();

            imageBatches.forEach( batch -> {
                ImageBatchDataExport batchDataExport = new ImageBatchDataExport();
                batchDataExport.setProtocol(batch.getIndividual().getGroup().getProtocol().getLabel());
                batchDataExport.setExperimentalGroup(batch.getIndividual().getGroup().getLabel());
                batchDataExport.setIndividual(batch.getIndividual().getLabel());
                batchDataExport.setDateTime(batch.getDate().toString());
                batchDataExport.setBatchId(batch.getId().toString());
                batchDataExport.setUser(batch.getUser().getFirstName() + " " + batch.getUser().getLastName());

                batch.getImageFiles().forEach( file -> {
                    Image image = file.getImage();
                    if (image == null) return;

                    Integer imageScaleValue = image.getScaleValue()!=null?image.getScaleValue():0;
                    Integer imageScalePixels = image.getScalePixels()!=null?image.getScalePixels():0;

                    ImageDataExport imageDataExport = new ImageDataExport();
                    imageDataExport.setImageFile(file.getName());
                    imageDataExport.setImage(image.getName());
                    imageDataExport.setMeasurementUnits(image.getMeasurementUnit());
                    imageDataExport.setReferenceScaleValue(imageScaleValue.toString());
                    imageDataExport.setReferenceScalePixels(imageScalePixels.toString());
                    imageDataExport.setConversionFactor(image.getMeasurementFactor().toString());
                    imageDataExport.setTotalAreaPixels(image.getTotalArea().toString());
                    imageDataExport.setTissueAreaPixels(image.getTotalTissueArea().toString());
                    imageDataExport.setViableTissueAreaPixels(image.getViableTissueArea().toString());
                    imageDataExport.setNecroticTissueAreaPixels(image.getNecroticTissueArea().toString());
                    imageDataExport.setTotalAreaUnits(Image.pixelsAreaToUnits(image.getTotalArea(),image.getMeasurementFactor()).toString());
                    imageDataExport.setTissueAreaUnits(Image.pixelsAreaToUnits(image.getTotalTissueArea(),image.getMeasurementFactor()).toString());
                    imageDataExport.setViableTissueAreaUnits(Image.pixelsAreaToUnits(image.getViableTissueArea(),image.getMeasurementFactor()).toString());
                    imageDataExport.setNecroticTissueAreaUnits(Image.pixelsAreaToUnits(image.getNecroticTissueArea(),image.getMeasurementFactor()).toString());
                    imageDataExport.setNecroticTissuePercentage(String.valueOf((image.getNecroticTissueArea()*100.0)/(image.getViableTissueArea()+image.getNecroticTissueArea())));

                    batchDataExport.getImages().add(imageDataExport);
                });

                batches.add(batchDataExport);
            });

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(writer,batches);
        }
        catch( Exception e ) {
            throw new DataExportException("data-export-exception",new String[]{e.getMessage()});
        }
    }

    @Data
    class ImageBatchDataExport {
        String protocol;
        String experimentalGroup;
        String individual;
        String dateTime;
        String batchId;
        String user;
        List<ImageDataExport> images = new ArrayList<>();
    }

    @Data
    class ImageDataExport {
        String imageFile;
        String image;
        String measurementUnits;
        String referenceScaleValue;
        String referenceScalePixels;
        String conversionFactor;
        String totalAreaPixels;
        String tissueAreaPixels;
        String viableTissueAreaPixels;
        String necroticTissueAreaPixels;
        String totalAreaUnits;
        String tissueAreaUnits;
        String viableTissueAreaUnits;
        String necroticTissueAreaUnits;
        String necroticTissuePercentage;
    }
}
