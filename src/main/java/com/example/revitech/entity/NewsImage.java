package com.example.revitech.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "news_images")
@Data
public class NewsImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    @Column(name = "content_type", length = 50)
    private String contentType;

    // 画像データ本体（Base64文字列）
    // SQL ServerのNVARCHAR(MAX)に対応させるため、定義を明示
    @Column(name = "image_data", columnDefinition = "NVARCHAR(MAX)") 
    private String imageData;

    // どのお知らせに属するか
    @ManyToOne
    @JoinColumn(name = "news_id")
    private News news;

    /**
     * Thymeleaf表示用のデータURIスキームを返すメソッド
     * HTML側で文字列結合を行うとSpELの文字数制限(100,000文字)に引っかかるため、
     * Java側で結合した文字列を返します。
     * Thymeleafからは ${image.dataUri} でアクセスできます。
     */
    public String getDataUri() {
        if (imageData != null && contentType != null) {
            return "data:" + contentType + ";base64," + imageData;
        }
        return null;
    }
}