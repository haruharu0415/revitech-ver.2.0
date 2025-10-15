// SignupForm.java の全文
package com.example.revitech.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupForm {

    // ▼▼▼【修正点】"username" から "name" に変更し、メッセージを修正 ▼▼▼
    @NotBlank(message = "名前は必須です")
    @Size(max = 20, message = "名前は20文字以内で入力してください")
    private String name;

    // ▼▼▼【新規追加】emailフィールドを追加 ▼▼▼
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレス形式で入力してください")
    private String email;

    @NotBlank(message = "パスワードは必須です")
    @Size(min = 8, message = "パスワードは8文字以上で入力してください")
    private String password;

    @NotBlank(message = "確認用パスワードは必須です")
    private String passwordConfirm;
}