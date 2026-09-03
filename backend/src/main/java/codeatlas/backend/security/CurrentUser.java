package codeatlas.backend.security;

import codeatlas.backend.exceptions.UnAuthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    public AppUserPrincipal require() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof AppUserPrincipal appUserPrincipal)) {
            throw new UnAuthorizedException("User not authenticated");
        }

        return appUserPrincipal;
    }
}