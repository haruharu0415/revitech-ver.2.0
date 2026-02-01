package com.example.revitech.form;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileEditForm {

    @NotBlank
    private String name;

    private String introduction;

    // アイコン画像（アップロード用）
    private MultipartFile iconFile;
    
    // ★★★ 追加: 先生の担当科目IDリスト ★★★
    private List<Integer> teacherSubjectIds;
}