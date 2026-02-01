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

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "news_body", nullable = false, columnDefinition = "TEXT")
    private String newsBody;

    @Column(name = "news_datetime")
    private LocalDateTime newsDatetime;
    
    @Column(name = "users_id")
    private Integer usersId;
    
    @Column(name = "sender_id")
    private Integer senderId;

    // ★★★ 修正: DBに存在するカラム「recipient_ids」をマッピング ★★★
    // ここには "1,2,3" のような文字列が入ります
    @Column(name = "recipient_ids")
    private String recipientIds;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsImage> images = new ArrayList<>();

    // ★★★ 修正: フォームからの入力受け取り用 (DBには保存しない) ★★★
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

    // ★★★ 追加: DB用カラムのGetter/Setter ★★★
    public String getRecipientIds() { return recipientIds; }
    public void setRecipientIds(String recipientIds) { this.recipientIds = recipientIds; }

    // ★★★ 追加: フォーム用リストのGetter/Setter ★★★
    public List<Integer> getRecipientUserIds() { return recipientUserIds; }
    public void setRecipientUserIds(List<Integer> recipientUserIds) { this.recipientUserIds = recipientUserIds; }
}