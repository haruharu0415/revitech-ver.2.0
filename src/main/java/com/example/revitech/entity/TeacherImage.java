package com.example.revitech.entity;

import java.util.Base64;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "teacher_images")
@Data
public class TeacherImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageId;

    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;

    @Lob
    @Column(name = "image_data")
    private byte[] imageData;

    public String getImageBase64() {
        if (imageData == null || imageData.length == 0) return null;
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageData);
    }
}