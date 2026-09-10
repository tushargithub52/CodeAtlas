package codeatlas.backend.config;

import codeatlas.backend.security.GithubOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;

/**
 * Security configuration — sets up OAuth2 GitHub login, session management, CORS, CSRF, and route authorization rules.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final GithubOAuth2UserService githubOAuth2UserService;

    /**
     * Redirects to the frontend callback page on successful GitHub OAuth2 login.
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(
            @Value("${app.frontend-url}") String frontendUrl) {
        SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setDefaultTargetUrl(frontendUrl + "/auth/callback");
        return successHandler;
    }

    /**
     * Redirects to the frontend login page with an error param on OAuth2 authentication failure.
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(
            @Value("${app.frontend-url}") String frontendUrl) {
        SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
        failureHandler.setDefaultFailureUrl(frontendUrl + "/login?error=oauth_failed");
        return failureHandler;
    }

    /**
     * Builds the security filter chain with session-based auth, OAuth2 login, logout, and route access rules.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler
    ) {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(auth ->
                    auth.requestMatchers("/api/auth/login-url",
                            "/oauth/**",
                            "/login/oauth2/**",
                            "/error").permitAll()
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .requestMatchers("/api/**").authenticated()
                            .anyRequest().permitAll()
                )
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .oauth2Login(oauth2 -> oauth2.userInfoEndpoint(userInfo ->
                        userInfo.userService(githubOAuth2UserService))
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
                )
                .logout(logout ->
                        logout.logoutUrl("/api/auth/logout")
                                .logoutSuccessHandler((request, response, authentication) ->
                                        response.setStatus(HttpStatus.NO_CONTENT.value())
                                )
                )
                .build();
    }
}
