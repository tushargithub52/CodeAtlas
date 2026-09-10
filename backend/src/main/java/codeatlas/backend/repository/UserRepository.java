package codeatlas.backend.repository;

import codeatlas.backend.enitity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository for User entities — provides lookup by GitHub user ID.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their GitHub numeric ID, used during OAuth2 login to upsert the user record.
     */
    Optional<User> findByGithubId(Long githubId);
}
