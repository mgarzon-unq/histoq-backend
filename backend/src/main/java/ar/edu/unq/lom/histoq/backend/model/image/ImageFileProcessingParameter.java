package ar.edu.unq.lom.histoq.backend.model.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "image_file_processing_parameter")
public class ImageFileProcessingParameter {
    private @Id
    @GeneratedValue
    Long id;
    private String name;
    private String value;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="file_id")
    ImageFile imageFile;

    public ImageFileProcessingParameter() {}
}
