package com.example.revitech.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {

    // ★★★ 注意: 画像保存先フォルダ。環境に合わせて変更してください ★★★
    // 例: C:/revitech_uploads/ または /var/www/uploads/ など
    private static final String UPLOAD_DIR = "C:/revitech_uploads/";

    @PostMapping("/api/chat/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 保存先ディレクトリがなければ作成
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // ファイル名の衝突を防ぐためUUIDを使用
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalFilename.substring(dotIndex);
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // ファイルを保存
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // ファイルタイプ判定
            String type = isImage(extension) ? "IMAGE" : "FILE";
            // アクセス用URL (WebMvcConfigでマッピングするパス)
            String fileUrl = "/uploads/" + newFilename;

            // レスポンスデータの作成
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("type", type);
            response.put("originalName", originalFilename);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("ファイルのアップロードに失敗しました");
        }
    }

    private boolean isImage(String extension) {
        String ext = extension.toLowerCase();
        return ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png") || ext.equals(".gif") || ext.equals(".webp");
    }
}