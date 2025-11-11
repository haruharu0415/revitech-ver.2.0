package com.example.revitech.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TeacherSubjectId implements Serializable {

    @Column(name = "users_id")
    private Long userId;

    @Column(name = "subject_id")
    private Long subjectId;

    public TeacherSubjectId() {}

    public TeacherSubjectId(Long userId, Long subjectId) {
        this.userId = userId;
        this.subjectId = subjectId;
    }

    // Getters, Setters, hashCode, equals
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeacherSubjectId that = (TeacherSubjectId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(subjectId, that.subjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, subjectId);
    }
}