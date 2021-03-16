package ar.edu.unq.lom.histoq.backend.model.storage;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class FileStorage {
    private @Id
    @GeneratedValue
    Long id;

    private String name;

    @ManyToOne
    private FileStorageType type;

    private boolean enabled;

    @Column(length=3000)
    private String serviceParameters;

    @Column(length=2000)
    private String applicationParameters;


    public FileStorage() {}

}
