package ehealth.group1.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

/**
 * The `EncoderConfig` class is a configuration class responsible for defining a bean that provides an instance
 * of the `Argon2PasswordEncoder`. This encoder is used for password encoding and decoding in Spring Security.
 * The `argon2PasswordEncoder()` method initializes and returns an instance of the `Argon2PasswordEncoder`
 * with default settings suitable for use with Spring Security version 5.8.
 */
@Configuration
public class EncoderConfig {

    @Bean
    public Argon2PasswordEncoder argon2PasswordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
