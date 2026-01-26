package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.TeacherHashtag;

public interface TeacherHashtagRepository extends JpaRepository<TeacherHashtag, Integer> {
    // 教員のIDでタグ一覧を取得
    List<TeacherHashtag> findByTeacherId(Integer teacherId);
    
    // ハッシュタグ名（完全一致）で検索
    List<TeacherHashtag> findByHashtag(String hashtag);
    
    // ハッシュタグ名（部分一致）で検索する場合
    List<TeacherHashtag> findByHashtagContaining(String hashtag);
}