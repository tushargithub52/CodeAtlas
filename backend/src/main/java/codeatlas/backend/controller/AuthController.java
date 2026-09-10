package codeatlas.backend.controller;

import codeatlas.backend.dto.UserResponse;
import codeatlas.backend.enitity.User;
import codeatlas.backend.security.AppUserPrincipal;
import codeatlas.backend.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for authentication endpoints — exposes the GitHub login URL and the current user's profile.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CurrentUser currentUser;

    /**
     * Returns the GitHub OAuth2 authorization URL for the frontend to initiate the login flow.
     */
    @GetMapping("/login-url")
    public Map<String, String> getLoginUrl() {
        return Map.of("url", "/oauth2/authorization/github");
    }

    /**
     * Returns the profile of the currently authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUserProfile() {
        AppUserPrincipal userPrincipal = currentUser.require();
        User user = userPrincipal.getUser();

        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getGithubId(),
                user.getGithubUsername(),
                user.getDisplayName(),
                user.getAvatarUrl()
        ));
    }
}
