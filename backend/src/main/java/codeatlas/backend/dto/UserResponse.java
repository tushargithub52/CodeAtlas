package codeatlas.backend.dto;

import java.util.UUID;

/**
 * DTO for returning the authenticated user's public profile to the client.
 */
public record UserResponse(
    UUID id,
    Long githubId,
    String githubUsername,
    String displayName,
    String avatarUrl
) {}
