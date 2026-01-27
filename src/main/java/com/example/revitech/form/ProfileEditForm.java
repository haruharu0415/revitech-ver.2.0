package com.example.revitech.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileEditForm {

    @NotBlank(message = "名前は必須です")
    @Size(max = 50, message = "名前は50文字以内で入力してください")
    private String name;

    // ★★★ 追加: 自己紹介 (教員のみ使用) ★★★
    private String introduction;

    // ★★★ 維持: アイコン画像ファイル ★★★
    private MultipartFile iconFile;
}