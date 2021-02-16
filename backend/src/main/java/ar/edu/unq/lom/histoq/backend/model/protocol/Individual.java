package ar.edu.unq.lom.histoq.backend.model.protocol;

import ar.edu.unq.lom.histoq.backend.model.image.ImageBatch;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Individual {
    private @Id
    @GeneratedValue
    Long id;
    String label;
    @JsonIgnore
    @ManyToOne
    ExperimentalGroup group;

    @JsonIgnore
    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="individual_id")
    private List<ImageBatch> batches = new ArrayList<ImageBatch>();

    public  Individual(){}
}
