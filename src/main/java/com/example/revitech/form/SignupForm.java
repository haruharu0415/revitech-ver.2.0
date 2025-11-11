package com.example.revitech.form; // パッケージ名は適宜変更してください

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class SignupForm {

    @NotEmpty(message = "名前を入力してください")
    private String name; // ★ ユーザー名 (name)

    @NotEmpty(message = "メールアドレスを入力してください")
    @Email(message = "有効なメールアドレスを入力してください")
    // ★ @jec.ac.jp のチェックはControllerで行うか、カスタムバリデーション
    private String email; // ★ メールアドレス

    @NotEmpty(message = "パスワードを入力してください")
    @Size(min = 8, message = "パスワードは8文字以上で入力してください") // 例: 最低8文字
    private String password;

    @NotEmpty(message = "パスワード(確認)を入力してください")
    private String passwordConfirm;

    // --- Getter / Setter ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPasswordConfirm() { return passwordConfirm; }
    public void setPasswordConfirm(String passwordConfirm) { this.passwordConfirm = passwordConfirm; }
    // --- Getter / Setter ここまで ---
}