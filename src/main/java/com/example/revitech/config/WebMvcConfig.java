package com.example.revitech.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ★修正: UsersServiceで保存している「srcフォルダ」を直接参照するように設定
        // "file:./" はプロジェクトのルートフォルダ (revitech-ver.2.0) を指します。
        // これにより、/images/icons/** へのアクセスが、実際の src/main/resources/static/images/icons/ フォルダに向くようになります。
        registry.addResourceHandler("/images/icons/**")
                .addResourceLocations("file:./src/main/resources/static/images/icons/");
    }
}