package ar.edu.unq.lom.histoq.backend.model.image;

import ar.edu.unq.lom.histoq.backend.model.protocol.Individual;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Image {
    private @Id
    @GeneratedValue
    Long            id;
    private String  name;
    @JsonIgnore
    @ManyToOne
    Individual individual;
    private Double  totalArea;
    private Double  totalTissueArea;
    private Double  viableTissueArea;
    private Double  necroticTissueArea;
    private String  measurementUnit;
    private Integer scaleValue;
    private Integer scalePixels;

    @OneToMany
    @JoinColumn(name="image_id")
    private List<ImageFile> files = new ArrayList<>();

    public Image(){}

    public Image(String name) {
        this.name = name;
    }

    public void addImageFile(ImageFile imageFile) {
        this.files.add(imageFile);
    }

    public Double getMeasurementFactor() {
        return ( getScaleValue()!=null && getScalePixels()!=null ?
                new Double(getScaleValue()) / new Double(getScalePixels())
                : 1 );
    }

    public static Double pixelsAreaToUnits(Double pixelsArea, double factor) {
        return pixelsArea * Math.pow(factor,2);
    }
}
