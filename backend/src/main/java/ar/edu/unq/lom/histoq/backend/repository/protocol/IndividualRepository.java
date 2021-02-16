package ar.edu.unq.lom.histoq.backend.repository.protocol;

import ar.edu.unq.lom.histoq.backend.model.protocol.Individual;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndividualRepository extends JpaRepository<Individual, Long> {
}
