package com.example.revitech.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ★★★ FileUploadControllerの UPLOAD_DIR と合わせること ★★★
        // Windowsの場合: file:C:/revitech_uploads/
        // Mac/Linuxの場合: file:/var/www/uploads/ など
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/revitech_uploads/");
    }
}