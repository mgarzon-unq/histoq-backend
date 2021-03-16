package ar.edu.unq.lom.histoq.backend.model.image;

import ar.edu.unq.lom.histoq.backend.model.storage.FileStorage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
    @Column (name = "auto_generated")
    private boolean generated = false;
    private boolean stitched = false;
    @JsonIgnore
    @ManyToOne
    private ImageBatch batch;
    @JsonIgnore
    @ManyToOne(cascade=CascadeType.ALL)
    private Image image;
    @OneToMany(cascade=CascadeType.ALL, mappedBy="file", orphanRemoval=true)
    private List<ImageFileProcessingParameter> customProcessingParameters = new ArrayList<ImageFileProcessingParameter>();
    @ManyToOne
    private FileStorage storage;
    private String idInStorage;

    public ImageFile() {}

    @JsonIgnore
    public String getStitchingGroup() {
        Pattern stringPattern = Pattern.compile("[a-zA-Z0-9]+");
        Matcher m = stringPattern.matcher(getName());
        return m.find() ? m.group() : "unknown";
    }

    public void updateCustomProcessingParameters(List<ImageFileProcessingParameter> customProcessingParameters) {
        customProcessingParameters.forEach(p-> setCustomProcessingParameter(p));
    }

    public void setCustomProcessingParameter(ImageFileProcessingParameter processingParameter) {

        if(processingParameter.getId()!=null) {
            Optional<ImageFileProcessingParameter> existingParameter = this.customProcessingParameters
                    .stream()
                    .filter( p -> p.getId().equals(processingParameter.getId()))
                    .findFirst();
            if(existingParameter.isPresent()) {
                existingParameter.get().setValue(processingParameter.getValue());
                return;
            }
        }

        addCustomProcessingParameter(processingParameter);
    }

    private void addCustomProcessingParameter(ImageFileProcessingParameter processingParameter) {
        this.customProcessingParameters.add(processingParameter);
        processingParameter.setFile(this);
    }

}
