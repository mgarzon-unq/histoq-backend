package ar.edu.unq.lom.histoq.backend.repository.image;

import ar.edu.unq.lom.histoq.backend.model.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}