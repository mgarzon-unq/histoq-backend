package ar.edu.unq.lom.histoq.backend.repository.user;

import ar.edu.unq.lom.histoq.backend.model.user.UserRegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRegistrationRequestRepository extends JpaRepository<UserRegistrationRequest, Long> {
    Optional<List<UserRegistrationRequest>> findByProcessed(boolean processed);
}
