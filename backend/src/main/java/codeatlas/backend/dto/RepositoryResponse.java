package codeatlas.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record RepositoryResponse (
    UUID id,
    Long githubRepoId,
    String owner,
    String name,
    String fullName,
    @JsonProperty("isPrivate") boolean isPrivate,
    String defaultBranch,
    String language,
    String htmlUrl,
    String description,
    String indexStatus,
    int chunkCount,
    int filesTotal,
    int filesProcessed,
    String errorMessage
) {}