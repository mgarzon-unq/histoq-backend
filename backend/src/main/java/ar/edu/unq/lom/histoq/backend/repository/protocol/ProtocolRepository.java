package ar.edu.unq.lom.histoq.backend.repository.protocol;

import ar.edu.unq.lom.histoq.backend.model.protocol.Protocol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProtocolRepository extends JpaRepository<Protocol, Long> {
    List<Protocol> findFirst20ByLabelContainingIgnoreCaseOrTitleContainingIgnoreCase(String labelSearchKey, String titleSearchKey);
}
