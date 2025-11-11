package com.example.revitech.repository;

import com.example.revitech.entity.TeacherComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherCommentRepository extends JpaRepository<TeacherComment, Integer> {
}