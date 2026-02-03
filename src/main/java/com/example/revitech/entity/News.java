package com.example.revitech.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Integer newsId;

    // ★★★ 修正箇所: NVARCHAR対応 ★★★
    @Column(name = "title", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String title;

    // ★★★ 修正箇所: NVARCHAR対応 (MAX) ★★★
    @Column(name = "news_body", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String newsBody;

    @Column(name = "news_datetime")
    private LocalDateTime newsDatetime;
    
    @Column(name = "users_id")
    private Integer usersId;
    
    @Column(name = "sender_id")
    private Integer senderId;

    @Column(name = "recipient_ids")
    private String recipientIds;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsImage> images = new ArrayList<>();

    @Transient
    private List<Integer> recipientUserIds;

    @PrePersist
    public void onPrePersist() {
        if (this.newsDatetime == null) {
            this.newsDatetime = LocalDateTime.now();
        }
    }

    public void addImage(NewsImage image) {
        images.add(image);
        image.setNews(this);
    }

    // --- Getters and Setters ---
    public Integer getNewsId() { return newsId; }
    public void setNewsId(Integer newsId) { this.newsId = newsId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getNewsBody() { return newsBody; }
    public void setNewsBody(String newsBody) { this.newsBody = newsBody; }

    public LocalDateTime getNewsDatetime() { return newsDatetime; }
    public void setNewsDatetime(LocalDateTime newsDatetime) { this.newsDatetime = newsDatetime; }

    public Integer getUsersId() { return usersId; }
    public void setUsersId(Integer usersId) { this.usersId = usersId; }

    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }

    public List<NewsImage> getImages() { return images; }
    public void setImages(List<NewsImage> images) { this.images = images; }

    public String getRecipientIds() { return recipientIds; }
    public void setRecipientIds(String recipientIds) { this.recipientIds = recipientIds; }

    public List<Integer> getRecipientUserIds() { return recipientUserIds; }
    public void setRecipientUserIds(List<Integer> recipientUserIds) { this.recipientUserIds = recipientUserIds; }
}