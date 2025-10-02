package com.example.demo.config;

import com.example.demo.security.AppUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // dùng @PreAuthorize nếu muốn chặn ở method
public class SecurityConfig {

    private final RoleBasedSuccessHandler successHandler;
    private final AppUserDetailsService userDetailsService;

    public SecurityConfig(RoleBasedSuccessHandler successHandler,
                          AppUserDetailsService userDetailsService) {
        this.successHandler = successHandler;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // public pages
                .requestMatchers("/login", "/do-login", "/logout", "/error").permitAll()

                // ✅ permit rõ ràng GET + POST cho /register
                .requestMatchers(HttpMethod.GET,  "/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/register").permitAll()

                // (Nếu sau này bạn bọc class bằng @RequestMapping("/auth"), thêm luôn 2 dòng này)
                .requestMatchers(HttpMethod.GET,  "/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()

                // static
                .requestMatchers("/css/**", "/js/**", "/img/**", "/assets/**").permitAll()

                // protected
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER","ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/do-login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(l -> l.logoutUrl("/logout").logoutSuccessUrl("/login?logout=true").permitAll())
            .exceptionHandling(e -> e.accessDeniedPage("/error-403"))
            .authenticationProvider(daoAuthenticationProvider());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}

@Component
class RoleBasedSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res,
                                        Authentication auth) throws IOException, ServletException {
        var roles = auth.getAuthorities().toString();
        if (roles.contains("ROLE_ADMIN")) {
            res.sendRedirect("/admin/home");
        } else if (roles.contains("ROLE_USER")) {
            res.sendRedirect("/user/home");
        } else {
            res.sendRedirect("/login?error=role");
        }
    }
}
