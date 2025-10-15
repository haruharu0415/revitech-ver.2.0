// SecurityConfig.java の全文
package com.example.revitech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true); 
        
        config.addAllowedOrigin("http://localhost:8080"); 
        config.addAllowedOrigin("http://127.0.0.1:8080"); 
        
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        source.registerCorsConfiguration("/**", config); 
        return new CorsFilter(source);
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {}) 
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**", "/js/**", "/webjars/**", "/images/**").permitAll() 
                
                // ▼▼▼【修正点】ここに "/home" を追加 ▼▼▼
                .requestMatchers("/", "/home", "/option", "/login", "/signup", "/teacher-list", "/terms").permitAll()
                
                .requestMatchers("/ws/**", "/app/**").permitAll() 
                
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login") 
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error") 
                .permitAll()
            )
            .logout(logout -> logout
            	    .logoutUrl("/logout") 
            	    .logoutSuccessUrl("/login?logout")
            	    .permitAll()
            	)
            
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/ws/**", "/app/**")
            );
        

        return http.build();
    }
}