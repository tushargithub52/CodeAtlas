package codeatlas.backend.service;

import codeatlas.backend.dto.IndexStatusResponse;
import codeatlas.backend.dto.RepositoryResponse;
import codeatlas.backend.enitity.Repository;
import codeatlas.backend.enitity.User;
import codeatlas.backend.exceptions.NotFoundException;
import codeatlas.backend.repository.RepositoryRepository;
import codeatlas.backend.service.github.GithubApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for syncing and querying GitHub repositories stored in the database for a given user.
 */
@Service
@RequiredArgsConstructor
public class RepositoryService {

    private final RepositoryRepository repositoryRepository;
    private final UserService userService;
    private final GithubApiClient githubApiClient;

    /**
     * Fetches all repos from GitHub for the user, upserts them into the database, and returns the sorted list.
     */
    @Transactional
    public List<RepositoryResponse> syncRepos(UUID userId) {
        User user = userService.requiredById(userId);
        String token = userService.decryptAccessToken(user.getAccessToken());
        List<Map<String, Object>> remoteRepos = githubApiClient.listUserRepos(token);

        List<Repository> saved = new ArrayList<>();

        for(Map<String, Object> remoteRepo : remoteRepos) {
            Long githubRepoId = toLong(remoteRepo.get("id"));
            Repository repo = repositoryRepository.findByUserIdAndGithubRepoId(userId, githubRepoId)
                    .orElseGet(Repository::new);

            String fullName = String.valueOf(remoteRepo.get("full_name"));
            String[] parts = fullName.split("/", 2);

            repo.setUserId(userId);
            repo.setGithubRepoId(githubRepoId);
            repo.setOwner(parts.length > 0 ? parts[0] : String.valueOf(remoteRepo.get("owner")));
            repo.setName(parts.length > 1 ? parts[1] : String.valueOf(remoteRepo.get("name")));
            repo.setFullName(fullName);
            repo.setPrivate(Boolean.TRUE.equals(remoteRepo.get("private")));
            repo.setDefaultBranch(remoteRepo.get("default_branch") != null ? String.valueOf(remoteRepo.get("default_branch")) : "main");
            repo.setLanguage(remoteRepo.get("language") != null ? String.valueOf(remoteRepo.get("language")) : null);
            repo.setHtmlUrl(remoteRepo.get("html_url") != null ? String.valueOf(remoteRepo.get("html_url")) : null);
            repo.setDescription(remoteRepo.get("description") != null ? String.valueOf(remoteRepo.get("description")) : null);
            repo.setUpdatedAt(Instant.now());
            if (repo.getOwner() == null || repo.getOwner().isBlank()) {
                Object ownerObj = remoteRepo.get("owner");
                if (ownerObj instanceof Map<?, ?> ownerMap && ownerMap.get("login") != null) {
                    repo.setOwner(String.valueOf(ownerMap.get("login")));
                }
            }
            saved.add(repositoryRepository.save(repo));
        }

        return saved.stream()
                .sorted((a, b) -> a.getFullName().compareToIgnoreCase(b.getFullName()))
                .map(this::toResponse)
                .toList();
    }

    /**
     * Returns all repositories for the user from the database, sorted alphabetically by full name.
     */
    @Transactional(readOnly = true)
    public List<RepositoryResponse> listRepos(UUID userId) {
        return repositoryRepository
                .findByUserIdOrderByFullNameAsc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Returns a repository by ID only if it belongs to the given user, throwing NotFoundException otherwise.
     */
    @Transactional(readOnly = true) 
    public Repository getOwnedRepo(UUID userId, UUID repoId) {
        return repositoryRepository
                .findByIdAndUserId(repoId, userId)
                .orElseThrow(() -> new NotFoundException("Repository not found"));
    }

    /**
     * Returns the current index status and progress metrics for the given repository.
     */
    @Transactional(readOnly = true)
    public IndexStatusResponse getIndexStatus(UUID repoId, UUID userId) {
        Repository repository = getOwnedRepo(userId, repoId);
        return  new IndexStatusResponse(
            repository.getId(),
            repository.getIndexStatus(),
            repository.getFilesTotal(),
            repository.getFilesProcessed(),
            repository.getChunkCount(),
            repository.getIndexedAt(),
            repository.getErrorMessage()
        );
    }

    public  RepositoryResponse toResponse(Repository repository) {
        return new RepositoryResponse(
                repository.getId(),
                repository.getGithubRepoId(),
                repository.getOwner(),
                repository.getName(),
                repository.getFullName(),
                repository.isPrivate(),
                repository.getDefaultBranch(),
                repository.getLanguage(),
                repository.getHtmlUrl(),
                repository.getDescription(),
                repository.getIndexStatus(),
                repository.getIndexedAt(),
                repository.getChunkCount(),
                repository.getFilesTotal(),
                repository.getFilesProcessed(),
                repository.getErrorMessage()
        );
    }

    private static Long toLong(Object value) {
        if(value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }
}
