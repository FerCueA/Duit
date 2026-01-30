package es.duit.app.config;

import es.duit.app.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

        private final CustomUserDetailsService customUserDetailsService;
        private final LoginSuccessHandler loginSuccessHandler;
        private final LoginFailureHandler loginFailureHandler;

        public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                        LoginSuccessHandler loginSuccessHandler,
                        LoginFailureHandler loginFailureHandler) {
                this.customUserDetailsService = customUserDetailsService;
                this.loginSuccessHandler = loginSuccessHandler;
                this.loginFailureHandler = loginFailureHandler;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/", "/index", "/public/**", "/login", "/registro",
                                                                "/register",
                                                                "/css/**", "/js/**", "/img/**", "/static/**",
                                                                "/privacidad", "/terminos", "/ayuda",
                                                                "/swagger-ui/**", "/v3/api-docs/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .usernameParameter("username")
                                                .passwordParameter("password")
                                                .successHandler(loginSuccessHandler)
                                                .failureHandler(loginFailureHandler)
                                                .permitAll())
                                .rememberMe(remember -> remember
                                                .key("duit-remember-me")
                                                .rememberMeParameter("remember-me")
                                                .tokenValiditySeconds(86400)
                                                .userDetailsService(customUserDetailsService))
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login?logout=true")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID", "remember-me")
                                                .permitAll());
                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

}
