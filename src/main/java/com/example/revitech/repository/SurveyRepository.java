package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.Survey;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Integer> {

    // 先生が作成したアンケートを新しい順に取得
    List<Survey> findByTeacherIdOrderByCreatedAtDesc(Integer teacherId);

    // ★重要: 生徒IDを指定して、その生徒が対象になっているアンケートを取得
    // Survey テーブルと SurveyTarget テーブルを結合して検索します
    @Query("SELECT s FROM Survey s JOIN SurveyTarget st ON s.surveyId = st.surveyId WHERE st.studentId = :studentId ORDER BY s.createdAt DESC")
    List<Survey> findByTargetStudentId(@Param("studentId") Integer studentId);
}