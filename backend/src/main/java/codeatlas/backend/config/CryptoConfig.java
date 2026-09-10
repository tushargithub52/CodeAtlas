package codeatlas.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * Crypto configuration — provides a TextEncryptor bean for encrypting and decrypting OAuth access tokens.
 */
@Configuration
public class CryptoConfig {

    /**
     * Creates a password-based text encryptor using the configured password and salt from application properties.
     */
    @Bean
    TextEncryptor tokenEncryptor(
            @Value("${app.token-encryptor-password}") String password,
            @Value("${app.token-encryptor-salt}") String salt) {
        return Encryptors.text(password, salt);
    }
}
