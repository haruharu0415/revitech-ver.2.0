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
import org.springframework.web.cors.CorsConfiguration; // 【追加】
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // 【追加】
import org.springframework.web.filter.CorsFilter; // 【追加】

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

    /**
     * 【追加】WebSocket接続のためのCORS設定
     * allowCredentials=trueの場合、AllowedOriginsに*を使用できない制約を回避するため、
     * 開発環境のlocalhostを明示的に許可する。
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 認証情報（セッションクッキー）の送信を許可
        config.setAllowCredentials(true); 
        
        // 【重要】localhostを明示的に許可
        config.addAllowedOrigin("http://localhost:8080"); 
        config.addAllowedOrigin("http://127.0.0.1:8080"); 
        
        // すべてのヘッダーとメソッドを許可
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        // すべてのパス ("/**") にこのCORS設定を適用
        source.registerCorsConfiguration("/**", config); 
        return new CorsFilter(source);
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 【修正】CorsFilter Beanを使用するためにCORSを有効化
            .cors(cors -> {}) 
            .authorizeHttpRequests(authorize -> authorize
                // 【最重要】静的リソース（WebJars, CSS, JS）を許可
                .requestMatchers("/css/**", "/js/**", "/webjars/**", "/images/**").permitAll() 
                
                // 【最重要】ログイン画面、サインアップ、および公開ページを許可
                .requestMatchers("/", "/option", "/login", "/signup", "/teacher-list", "/terms").permitAll()
                
                // WebSocketエンドポイントも許可
                .requestMatchers("/ws/**", "/app/**").permitAll() 
                
                // 上記以外は認証が必要
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
            
            // WebSocket/STOMPのエンドポイントでCSRF保護を無効化
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/ws/**", "/app/**")
            );
        

        return http.build();
    }
}