package ar.edu.unq.lom.histoq.backend.model.user;

import ar.edu.unq.lom.histoq.backend.model.image.ImageBatch;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class User {
    private @Id
    @GeneratedValue
    Long            id;
    private String  firstName;
    private String  lastName;
    @Column(unique=true)
    private String  email;
    private boolean admin = false;
    private boolean active = true;

    @JsonIgnore
    @OneToMany(cascade= CascadeType.ALL)
    @JoinColumn(name="user_id")
    private List<ImageBatch> batches;

    public User(){}

    public User(String firstName, String lastName, String email, boolean admin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.admin = admin;
    }
}
