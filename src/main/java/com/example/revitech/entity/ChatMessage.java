package com.example.revitech.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(name = "receiver_student_id", nullable = false)
    private Long receiverStudentId;

    @Column(name = "sender_student_id", nullable = false)
    private Long senderStudentId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ChatMessage() {}

    public ChatMessage(String content, Long receiverStudentId, Long senderStudentId) {
        this.content = content;
        this.receiverStudentId = receiverStudentId;
        this.senderStudentId = senderStudentId;
    }

    // Before saving, set the createdAt timestamp
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getReceiverStudentId() {
        return receiverStudentId;
    }

    public void setReceiverStudentId(Long receiverStudentId) {
        this.receiverStudentId = receiverStudentId;
    }

    public Long getSenderStudentId() {
        return senderStudentId;
    }

    public void setSenderStudentId(Long senderStudentId) {
        this.senderStudentId = senderStudentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
