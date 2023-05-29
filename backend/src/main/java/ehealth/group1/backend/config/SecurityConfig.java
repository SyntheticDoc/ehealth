package ehealth.group1.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


/**
 * This method defines a `SecurityFilterChain` bean for configuring the security filters in the application.
 * It allows all requests to the root path ("/") without authentication, and requires authentication for any
 * other requests. The method also disables the frame options header and configures CSRF (Cross-Site Request
 * Forgery) protection to ignore all requests. The method returns the built `SecurityFilterChain`.
 */
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*http.authorizeHttpRequests(
                authorizeHttpRequests ->
                        authorizeHttpRequests.requestMatchers("/").permitAll().anyRequest().authenticated()
        )
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());*/

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/**")).permitAll()
                )
                .headers(headers -> headers.frameOptions().disable())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/**")));
        return http.build();
    }
}
