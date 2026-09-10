package codeatlas.backend.enitity;

/**
 * Enum representing the possible states of a repository's code indexing pipeline.
 */
public enum IndexStatus {
    PENDING,
    INDEXING,
    READY,
    FAILED
}
