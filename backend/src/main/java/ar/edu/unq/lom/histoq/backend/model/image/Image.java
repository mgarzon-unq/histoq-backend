package ar.edu.unq.lom.histoq.backend.model.image;

import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
public class Image {
    private @Id
    @GeneratedValue
    Long            id;
    private String  name;
    private Double  totalArea;
    private Double  totalTissueArea;
    private Double  viableTissueArea;
    private Double  necroticTissueArea;
    private String  measurementUnit;
    private Integer scaleValue;
    private Integer scalePixels;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="image", orphanRemoval=true)
    private List<ImageFile> files = new ArrayList<>();

    public Image(){}

    public Image(String name) {
        this.name = name;
    }

    public void addImageFile(ImageFile imageFile) {
        this.files.add(imageFile);
        imageFile.setImage(this);
    }
    public void removeImageFile(ImageFile imageFile) {
        this.files.remove(imageFile);
        imageFile.setImage(null);
    }

    public Double getMeasurementFactor() {
        return ( getScaleValue()!=null && getScalePixels()!=null ?
                new Double(getScaleValue()) / new Double(getScalePixels())
                : 1 );
    }

    public ImageFile getMainImageFile() {
        return getFiles()
                .stream()
                .filter(f -> f.isGenerated() && !f.isStitched())
                .collect(Collectors.toList())
                .get(0);
    }

    public static Double pixelsAreaToUnits(Double pixelsArea, double factor) {
        return pixelsArea * Math.pow(factor,2);
    }
}
