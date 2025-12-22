package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.TeacherImage;

public interface TeacherImageRepository extends JpaRepository<TeacherImage, Integer> {
    List<TeacherImage> findByTeacherId(Integer teacherId);
}