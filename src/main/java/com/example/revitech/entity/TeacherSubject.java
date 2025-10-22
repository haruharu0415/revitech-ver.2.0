package com.example.revitech.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "teacher_subject")
public class TeacherSubject {

    @EmbeddedId
    private TeacherSubjectId id;

    // // 関連エンティティへのマッピング (任意)
    // @ManyToOne
    // @MapsId("userId")
    // @JoinColumn(name = "users_id")
    // private Users teacher;

    // @ManyToOne
    // @MapsId("subjectId")
    // @JoinColumn(name = "subject_id")
    // private Subject subject;

    public TeacherSubject() {}

    public TeacherSubject(TeacherSubjectId id) {
        this.id = id;
    }

     public TeacherSubject(Long userId, Long subjectId) {
        this.id = new TeacherSubjectId(userId, subjectId);
    }

    // Getters and Setters
    public TeacherSubjectId getId() { return id; }
    public void setId(TeacherSubjectId id) { this.id = id; }

    // id内のフィールドへの簡易アクセス用 (任意)
    public Long getUserId() { return this.id != null ? this.id.getUserId() : null; }
    public Long getSubjectId() { return this.id != null ? this.id.getSubjectId() : null; }
}