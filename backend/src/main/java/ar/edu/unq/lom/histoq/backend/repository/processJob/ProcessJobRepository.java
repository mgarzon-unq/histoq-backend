package ar.edu.unq.lom.histoq.backend.repository.processJob;

import ar.edu.unq.lom.histoq.backend.model.processJob.ProcessJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessJobRepository extends JpaRepository<ProcessJob, Long> {
}
