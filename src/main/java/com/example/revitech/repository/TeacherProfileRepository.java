package com.example.revitech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.TeacherProfile;

public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Integer> {
    Optional<TeacherProfile> findByTeacherId(Integer teacherId);
}