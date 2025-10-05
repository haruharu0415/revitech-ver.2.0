package com.example.revitech.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupForm {

    @NotBlank(message = "ユーザー名は必須です")
    @Size(min = 4, max = 20, message = "ユーザー名は4文字以上20文字以内で入力してください")
    private String username;

    @NotBlank(message = "パスワードは必須です")
    @Size(min = 8, message = "パスワードは8文字以上で入力してください")
    private String password;

    @NotBlank(message = "確認用パスワードは必須です")
    private String passwordConfirm;
}
