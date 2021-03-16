package ar.edu.unq.lom.histoq.backend.repository.storage;

import ar.edu.unq.lom.histoq.backend.model.storage.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileStorageRepository extends JpaRepository<FileStorage, Long> {
    List<FileStorage> findByEnabledTrue();
}
