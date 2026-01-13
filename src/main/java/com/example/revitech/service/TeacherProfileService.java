package com.example.revitech.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.revitech.entity.TeacherHashtag;
import com.example.revitech.entity.TeacherImage;
import com.example.revitech.entity.TeacherProfile;
import com.example.revitech.repository.TeacherHashtagRepository;
import com.example.revitech.repository.TeacherImageRepository;
import com.example.revitech.repository.TeacherProfileRepository;

@Service
@Transactional
public class TeacherProfileService {

    private final TeacherProfileRepository profileRepository;
    private final TeacherImageRepository imageRepository;
    private final TeacherHashtagRepository hashtagRepository;

    public TeacherProfileService(TeacherProfileRepository profileRepository, 
                                 TeacherImageRepository imageRepository,
                                 TeacherHashtagRepository hashtagRepository) {
        this.profileRepository = profileRepository;
        this.imageRepository = imageRepository;
        this.hashtagRepository = hashtagRepository;
    }

    public TeacherProfile getProfile(Integer teacherId) {
        return profileRepository.findByTeacherId(teacherId).orElse(new TeacherProfile());
    }

    public List<TeacherImage> getImages(Integer teacherId) {
        return imageRepository.findByTeacherId(teacherId);
    }
    
    // ★ 追加: ハッシュタグ取得
    public List<TeacherHashtag> getHashtags(Integer teacherId) {
        return hashtagRepository.findByTeacherId(teacherId);
    }

    // ★ 追加: ハッシュタグ保存
    public void addHashtag(Integer teacherId, String hashtag) {
        if (hashtag == null || hashtag.trim().isEmpty()) return;
        
        // "#" がついていなければ自動付与
        String tag = hashtag.trim();
        if (!tag.startsWith("#")) {
            tag = "#" + tag;
        }

        TeacherHashtag th = new TeacherHashtag();
        th.setTeacherId(teacherId);
        th.setHashtag(tag);
        hashtagRepository.save(th);
    }

    // ★ 追加: ハッシュタグ削除
    public void deleteHashtag(Integer hashtagId) {
        hashtagRepository.deleteById(hashtagId);
    }

    // プロフィール保存
    public void saveProfile(Integer teacherId, String introduction, MultipartFile iconFile) {
        TeacherProfile profile = profileRepository.findByTeacherId(teacherId).orElse(new TeacherProfile());
        if (profile.getTeacherId() == null) profile.setTeacherId(teacherId);
        profile.setIntroduction(introduction);
        if (iconFile != null && !iconFile.isEmpty()) {
            try {
                profile.setIconData(iconFile.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload icon", e);
            }
        }
        profileRepository.save(profile);
    }

    // アイコンのみ更新
    public void updateIconOnly(Integer teacherId, MultipartFile iconFile) {
        if (iconFile == null || iconFile.isEmpty()) return;
        TeacherProfile profile = profileRepository.findByTeacherId(teacherId).orElse(new TeacherProfile());
        if (profile.getTeacherId() == null) profile.setTeacherId(teacherId);
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
    
    public void deleteImage(Integer imageId) {
        imageRepository.deleteById(imageId);
    }
}