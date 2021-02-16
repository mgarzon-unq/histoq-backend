package ar.edu.unq.lom.histoq.backend.model.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity (name = "experimental_group")
public class ExperimentalGroup {
    private @Id
    @GeneratedValue
    Long id;
    String label;
    @JsonIgnore
    @ManyToOne
    Protocol protocol;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="group_id")
    private List<Individual> individuals;

    public ExperimentalGroup(){}

    public void addIndividual(Individual individual) {
        if( this.individuals==null )
            this.individuals=new ArrayList<Individual>();
        this.individuals.add(individual);
    }
}
