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
    private String introduction; //
    // 画像ファイル（変更しない場合は空で来ることもあるので必須チェックは外します）
    private MultipartFile iconFile;
}