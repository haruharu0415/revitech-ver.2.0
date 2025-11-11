package com.example.revitech.repository;

// import java.util.UUID; // ★ UUID は使わない
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.TeacherProfile;

// ★ 主キーの型を Long に戻す ★
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {
}