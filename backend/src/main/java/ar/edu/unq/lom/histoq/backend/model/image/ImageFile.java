package ar.edu.unq.lom.histoq.backend.model.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Data
@Entity (name = "image_file")
public class ImageFile {
    private @Id
    @GeneratedValue
    Long id;
    private String name;
    private boolean stitched = false;
    @JsonIgnore
    @ManyToOne
    ImageBatch batch;
    @JsonIgnore
    @ManyToOne(cascade=CascadeType.ALL)
    Image image;
    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="file_id")
    private List<ImageFileProcessingParameter> customProcessingParameters = new ArrayList<>();

    public ImageFile() {}

    @JsonIgnore
    public String getStitchingGroup() {
        Pattern stringPattern = Pattern.compile("[a-zA-Z0-9]+");
        Matcher m = stringPattern.matcher(getName());
        return m.find() ? m.group() : "unknown";
    }
}
