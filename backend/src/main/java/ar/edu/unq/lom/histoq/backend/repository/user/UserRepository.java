package ar.edu.unq.lom.histoq.backend.repository.user;

import ar.edu.unq.lom.histoq.backend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
