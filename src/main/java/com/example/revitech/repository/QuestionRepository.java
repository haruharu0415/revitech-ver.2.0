package com.example.revitech.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}