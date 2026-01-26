package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.BanWord;

@Repository
public interface BanWordRepository extends JpaRepository<BanWord, Integer> {
    List<BanWord> findByTeacherId(Integer teacherId);
    boolean existsByTeacherIdAndWord(Integer teacherId, String word);
}