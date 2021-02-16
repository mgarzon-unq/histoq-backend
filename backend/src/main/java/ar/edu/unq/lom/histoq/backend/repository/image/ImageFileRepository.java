package ar.edu.unq.lom.histoq.backend.repository.image;

import ar.edu.unq.lom.histoq.backend.model.image.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
}