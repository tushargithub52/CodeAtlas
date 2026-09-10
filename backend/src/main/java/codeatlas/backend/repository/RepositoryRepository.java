package codeatlas.backend.repository;

import codeatlas.backend.enitity.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository for Repository entities — provides queries scoped to a specific user's repositories.
 */
public interface RepositoryRepository extends JpaRepository<Repository, UUID> {

    /**
     * Returns all repositories for the given user, sorted alphabetically by full name.
     */
    List<Repository> findByUserIdOrderByFullNameAsc(UUID userId);

    /**
     * Returns a repository only if it belongs to the specified user, used for ownership-safe lookups.
     */
    Optional<Repository> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Finds an existing repository by user and GitHub repo ID, used during sync to avoid duplicates.
     */
    Optional<Repository> findByUserIdAndGithubRepoId(UUID userId, Long githubRepoId);
}
