package com.example.revitech.entity;

import java.time.LocalDateTime;
import java.util.List; // ★追加: Listを使用するため

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient; // ★追加: DBに永続化しないフィールドであることを示す
import lombok.Data; 

@Entity
@Table(name = "news")
@Data
public class News {
    
    // ニュースIDを主キーとする
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id", nullable = false)
    private Integer newsId; // お知らせの主キー
    
    // 送信者（教員）のID
    @Column(name = "sender_id", nullable = false) 
    private Integer senderId; // 例: 教員ユーザーのID
    
    // お知らせの本文（内容）
    @Column(name= "news_body", nullable = false, columnDefinition = "TEXT") 
    private String newsBody;
    
    // お知らせの送信（登録）日時
    @Column(name = "news_datetime", nullable = false)
    private LocalDateTime newsDatetime;
    
    // タイトル
    @Column(name = "title", nullable = false)
    private String title;

    // 【★★ 修正・新規追加 ★★】
    // フォームでのデータ受け渡し用（DB非永続化）。
    // この名前（recipientUserIds）とlist.html、register.htmlのth:field名を一致させます。
    @Transient 
    private List<Integer> recipientUserIds;
}