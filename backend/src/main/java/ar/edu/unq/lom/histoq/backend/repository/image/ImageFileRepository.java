package ar.edu.unq.lom.histoq.backend.repository.image;

import ar.edu.unq.lom.histoq.backend.model.image.ImageFile;
import ar.edu.unq.lom.histoq.backend.model.storage.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
}