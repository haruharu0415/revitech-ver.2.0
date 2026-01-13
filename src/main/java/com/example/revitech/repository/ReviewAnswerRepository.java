package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ReviewAnswer;

@Repository
public interface ReviewAnswerRepository extends JpaRepository<ReviewAnswer, Integer> {

    /**
     * ★ここが修正ポイント★
     * ReviewAnswer(点数)テーブルには teacherId がないので、
     * TeacherReview(親)テーブルと reviewId でくっつけて、そこから teacherId を探します。
     * * 古い書き方: findByTeacherId(Integer teacherId); <- これだと動かないことがあります
     */
    @Query("SELECT ra FROM ReviewAnswer ra, TeacherReview tr WHERE ra.reviewId = tr.reviewId AND tr.teacherId = :teacherId")
    List<ReviewAnswer> findByTeacherId(@Param("teacherId") Integer teacherId);
}