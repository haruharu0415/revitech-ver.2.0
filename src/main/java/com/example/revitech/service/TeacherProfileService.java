package com.example.revitech.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.revitech.entity.TeacherImage;
import com.example.revitech.entity.TeacherProfile;
import com.example.revitech.repository.TeacherImageRepository;
import com.example.revitech.repository.TeacherProfileRepository;

@Service
@Transactional
public class TeacherProfileService {

    private final TeacherProfileRepository profileRepository;
    private final TeacherImageRepository imageRepository;

    public TeacherProfileService(TeacherProfileRepository profileRepository, TeacherImageRepository imageRepository) {
        this.profileRepository = profileRepository;
        this.imageRepository = imageRepository;
    }

    public TeacherProfile getProfile(Integer teacherId) {
        return profileRepository.findByTeacherId(teacherId).orElse(new TeacherProfile());
    }

    public List<TeacherImage> getImages(Integer teacherId) {
        return imageRepository.findByTeacherId(teacherId);
    }

    // プロフィール保存（アイコン含む）
    public void saveProfile(Integer teacherId, String introduction, MultipartFile iconFile) {
        TeacherProfile profile = profileRepository.findByTeacherId(teacherId).orElse(new TeacherProfile());
        
        // 新規作成時はIDセット
        if (profile.getTeacherId() == null) {
            profile.setTeacherId(teacherId);
        }
        
        profile.setIntroduction(introduction);

        // ファイルがある場合のみDBにバイナリ保存
        if (iconFile != null && !iconFile.isEmpty()) {
            try {
                profile.setIconData(iconFile.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload icon", e);
            }
        }

        profileRepository.save(profile);
    }

    // ★★★ 追加: アイコンのみ更新（review.htmlからの変更用） ★★★
    public void updateIconOnly(Integer teacherId, MultipartFile iconFile) {
        if (iconFile == null || iconFile.isEmpty()) return;

        TeacherProfile profile = profileRepository.findByTeacherId(teacherId).orElse(new TeacherProfile());
        
        if (profile.getTeacherId() == null) {
            profile.setTeacherId(teacherId);
        }

        try {
            profile.setIconData(iconFile.getBytes());
            profileRepository.save(profile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update icon", e);
        }
    }

    // カルーセル画像追加
    public void addCarouselImages(Integer teacherId, MultipartFile[] files) {
        if (files == null) return;

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    TeacherImage img = new TeacherImage();
                    img.setTeacherId(teacherId);
                    img.setImageData(file.getBytes());
                    imageRepository.save(img);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload image", e);
                }
            }
        }
    }
    
    // カルーセル画像の個別削除
    public void deleteImage(Integer imageId) {
        imageRepository.deleteById(imageId);
    }
}