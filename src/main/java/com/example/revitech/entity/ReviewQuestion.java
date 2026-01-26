package com.example.revitech.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "review_question")
@Data
public class ReviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "review_id", nullable = false)
    private Integer reviewId;

    @Column(name = "question_id", nullable = false)
    private Integer questionId;
}