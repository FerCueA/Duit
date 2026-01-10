package es.duit.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import es.duit.connections.UsuarioBD;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private UsuarioBD usuarioBD;

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .userDetailsService(usuarioBD)
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                        "/", "/index",
                                        "/login", "/loginprocess",
                                        "/register",
                                        "/css/**", "/js/**", "/images/**"
                                ).permitAll()
                                .requestMatchers("/home").authenticated()
                                .anyRequest().authenticated()
                        )
                        .formLogin(form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/loginprocess")
                                .defaultSuccessUrl("/home", true)
                                .permitAll()
                        )
                        .logout(logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/index")
                                .permitAll()
                        );
                return http.build();
        }

        // si nos conectamos con usuarios en memoria o con base de datos necesitamos un
        // encoder si o si
        // este funcuiona para usuarios en memoria sin encriptar y en una base de datos
        // tambien sin encriptar
        @Bean
        @SuppressWarnings("deprecation")
        PasswordEncoder passwordEncoder() {
                PasswordEncoder encoder = NoOpPasswordEncoder.getInstance();
                return encoder;
        }

}
