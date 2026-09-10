package codeatlas.backend.service;

import codeatlas.backend.enitity.User;
import codeatlas.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

/**
 * Service handling user persistence — creates or updates a user record after GitHub OAuth2 login.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TextEncryptor tokenEncryptor;

    /**
     * Creates or updates a user from GitHub OAuth2 attributes, encrypting and storing the access token.
     */
    @Transactional
    public User upsertUserFromGithub(Map<String, Object> attributes, String accessToken, String scopes) {
        Long githubId = toLong(attributes.get("id"));
        String githubUsername = String.valueOf(attributes.get("login"));
        String displayName = attributes.get("name") != null
                ? String.valueOf(attributes.get("name")) : githubUsername;
        String avatarUrl = String.valueOf(attributes.get("avatar_url"));

        String encryptedAccessToken = encryptAccessToken(accessToken);

        User user = userRepository.findByGithubId(githubId).orElseGet(User::new);
        user.setGithubId(githubId);
        user.setGithubUsername(githubUsername);
        user.setDisplayName(displayName);
        user.setAvatarUrl(avatarUrl);
        user.setAccessToken(encryptedAccessToken);
        user.setTokenScopes(scopes);

        return userRepository.save(user);
    }

    /**
     * Looks up a user by ID and throws if not found — used internally when a valid user is required.
     */
    @Transactional(readOnly = true)
    public User requiredById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private String encryptAccessToken(String accessToken) {
        return tokenEncryptor.encrypt(accessToken);
    }

    /**
     * Decrypts a previously encrypted GitHub access token for use in API calls.
     */
    public String decryptAccessToken(String encryptedAccessToken) {
        return tokenEncryptor.decrypt(encryptedAccessToken);
    }

    private static Long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }
}
