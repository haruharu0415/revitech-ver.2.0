package com.example.revitech.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data; 

@Entity
@Table(name = "news")
@Data
public class News {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id", nullable = false)
    private Integer newsId;
    
    // 現在使っている送信者ID
    @Column(name = "sender_id", nullable = false) 
    private Integer senderId;
    
    // 【★追加】 データベースが要求している users_id をここに追加
    // これでHibernateがこの列を認識できるようになります
    @Column(name = "users_id", nullable = false)
    private Integer usersId;
    
    @ManyToOne
    @JoinColumn(name = "sender_id", insertable = false, updatable = false)
    private Users sender;
    
    @Column(name= "news_body", nullable = false, columnDefinition = "NVARCHAR(MAX)") 
    private String newsBody;
    
    @Column(name = "news_datetime", nullable = false)
    private LocalDateTime newsDatetime;
    
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "recipient_ids")
    private String recipientIds;

    @Transient 
    private List<Integer> recipientUserIds = new ArrayList<>();

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsImage> images = new ArrayList<>();

    public void addImage(NewsImage image) {
        images.add(image);
        image.setNews(this);
    }

    @PrePersist
    @PreUpdate
    public void convertListToString() {
        if (recipientUserIds != null && !recipientUserIds.isEmpty()) {
            this.recipientIds = recipientUserIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        } else {
            this.recipientIds = null;
        }
    }

    @PostLoad
    public void convertStringToList() {
        if (StringUtils.hasText(this.recipientIds)) {
            try {
                this.recipientUserIds = Arrays.stream(this.recipientIds.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                this.recipientUserIds = new ArrayList<>();
            }
        } else {
            this.recipientUserIds = new ArrayList<>();
        }
    }
}