package codeatlas.backend.security;

import codeatlas.backend.exceptions.UnAuthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Component for retrieving the authenticated user from the security context — throws 401 if not authenticated.
 */
@Component
public class CurrentUser {

    /**
     * Returns the current authenticated principal or throws UnAuthorizedException if no session exists.
     */
    public AppUserPrincipal require() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof AppUserPrincipal appUserPrincipal)) {
            throw new UnAuthorizedException("User not authenticated");
        }

        return appUserPrincipal;
    }
}