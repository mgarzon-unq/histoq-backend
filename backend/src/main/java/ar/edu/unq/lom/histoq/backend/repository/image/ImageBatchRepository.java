package ar.edu.unq.lom.histoq.backend.repository.image;

import ar.edu.unq.lom.histoq.backend.model.image.ImageBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageBatchRepository extends JpaRepository<ImageBatch, Long> {
    public List<ImageBatch> findAllByUserId(Long userId);
}
