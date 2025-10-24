package com.example.revitech.repository;

// import java.util.UUID; // ★ UUID は使わない
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.StudentProfile;

// ★ 主キーの型を Long に戻す ★
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
}