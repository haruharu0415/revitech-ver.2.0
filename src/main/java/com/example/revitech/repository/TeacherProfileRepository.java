package com.example.revitech.repository; // パッケージ名は実際の場所に合わせてください

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.TeacherProfile; // TeacherProfile エンティティをインポート

// JpaRepository<エンティティクラス名, 主キーの型> を継承します
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {

    // 基本的な CRUD 操作 (save, findById, findAll, deleteById など) は
    // JpaRepository が提供してくれるので、ここに追加のメソッド定義は不要です。

    // もし、特定のフィールド (例: department) で検索したい場合は、
    // 以下のようにメソッドを追加できます:
    // Optional<TeacherProfile> findByDepartment(String department);

}