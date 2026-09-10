package codeatlas.backend.dto;

import java.time.Instant;
import java.util.UUID;

import codeatlas.backend.enitity.IndexStatus;

public record IndexStatusResponse(
    UUID repositoryId,
    IndexStatus indexStatus,
    int filesTotal,
    int filesProcessed,
    int chunkCount,
    Instant indexedAt,
    String errorMessage
) {} 
