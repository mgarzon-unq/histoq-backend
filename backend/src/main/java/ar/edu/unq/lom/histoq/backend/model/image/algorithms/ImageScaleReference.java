package ar.edu.unq.lom.histoq.backend.model.image.algorithms;

import lombok.Data;

@Data
public class ImageScaleReference {
    private String measurementUnit = "px";
    private Integer scaleValue;
    private Integer scalePixels;

    public ImageScaleReference(){}

    public ImageScaleReference(String measurementUnit, Integer scaleValue, Integer scalePixels) {
        this.setMeasurementUnit(measurementUnit);
        this.setScaleValue(scaleValue);
        this.setScalePixels(scalePixels);
    }

    public boolean found() {
        return getScaleValue() != null;
    }
}
