package ar.edu.unq.lom.histoq.backend.model.storage;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class FileStorageType {
    private @Id
    @GeneratedValue
    Long id;

    private String name;

    public FileStorageType() {}
}
