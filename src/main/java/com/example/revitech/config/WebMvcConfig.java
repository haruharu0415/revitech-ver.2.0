package com.example.revitech.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. ユーザーホームディレクトリを取得 (例: C:\Users\Sora)
        String homeDir = System.getProperty("user.home");
        
        // 2. パスの区切り文字を統一してURL形式に変換
        // このパスは、homeDir/revitech_uploads/images/icons/ を指します。
        String path = "file:///" + homeDir.replace("\\", "/") + "/revitech_uploads/images/icons/";

        // 3. マッピング設定
        // 「/images/icons/**」へのアクセスを、上記の実フォルダへ誘導
        registry.addResourceHandler("/images/icons/**")
                .addResourceLocations(path);
    }
}