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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // ★ /login, /signup, CSS/JSなどは認証なしでアクセス許可
                .requestMatchers("/login", "/signup", "/css/**", "/js/**", "/webjars/**").permitAll()
                // 他のURLは認証が必要
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login") // ログインページのパス
                .loginProcessingUrl("/login") // ログインフォームのPOST先
                // ★ ユーザー名として受け取るパラメータ名を変更
                .usernameParameter("usernameOrEmail")
                .defaultSuccessUrl("/home", true) // ログイン成功時の遷移先
                .failureUrl("/login?error=true") // ログイン失敗時の遷移先
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true") // ログアウト成功時の遷移先
                .permitAll()
            );
            // .csrf(csrf -> csrf.disable()); // CSRF保護を無効にする場合 (非推奨)

        return http.build();
    }
}
  