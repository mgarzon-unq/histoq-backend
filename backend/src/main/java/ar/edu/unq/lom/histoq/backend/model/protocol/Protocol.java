package ar.edu.unq.lom.histoq.backend.model.protocol;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Protocol {
    private @Id
    @GeneratedValue
    Long id;
    private String label;
    private String title;
    private Date date;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="protocol_id")
    private List<ExperimentalGroup> experimentalGroups;

    public Protocol() {}

    public Protocol(String label, String title, Date date) {
        this.label = label;
        this.title = title;
        this.date  = date;
    }

    public void addExperimentalGroup(ExperimentalGroup group) {
        if(this.experimentalGroups==null)
            this.experimentalGroups=new ArrayList<ExperimentalGroup>();
        this.experimentalGroups.add(group);
    }
}
