package com.example.revitech.form;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // 使う場合のみ
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupForm {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 4, max = 100)
    private String password;
    
    // パスワード確認用フィールドがない場合は追加してください
    private String passwordConfirm; 

    @NotNull
    private Integer role; // 1:Student, 2:Teacher

    // ★修正: ここに @NotNull がついていたら必ず削除してください！
    // 先生の場合はnullになるため、必須チェックをつけると登録できません。
    private Integer subjectId;

    // 先生用の担当科目リスト
    private List<Integer> teacherSubjectIds;
}