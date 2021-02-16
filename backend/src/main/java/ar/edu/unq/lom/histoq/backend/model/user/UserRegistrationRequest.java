package ar.edu.unq.lom.histoq.backend.model.user;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
public class UserRegistrationRequest {
    private @Id
    @GeneratedValue
    Long   id;
    private String  firstName;
    private String  lastName;
    private String  socialId;
    private String  message;
    private Date    requestDate = new Date();
    private boolean processed = false;

    public UserRegistrationRequest(){}

    public UserRegistrationRequest(String firstName, String lastName, String socialId) {
        this(firstName,lastName,socialId,null);
    }

    public UserRegistrationRequest(String firstName, String lastName, String socialId, String message) {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setSocialId(socialId);
        this.setMessage(message);
    }
}
