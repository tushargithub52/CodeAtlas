package codeatlas.backend.dto;

import java.time.Instant;
import java.util.UUID;

import codeatlas.backend.enitity.IndexStatus;

/**
 * DTO for returning the current indexing status and file processing progress of a repository.
 */
public record IndexStatusResponse(
    UUID repositoryId,
    IndexStatus indexStatus,
    int filesTotal,
    int filesProcessed,
    int chunkCount,
    Instant indexedAt,
    String errorMessage
) {} 
