package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.News;

@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {
    
    // 日付の降順（新しい順）で全件取得
    List<News> findAllByOrderByNewsDatetimeDesc();

    // ★★★ 追加: 最新のニュースを指定件数（Top N）だけ取得 ★★★
    List<News> findTop3ByOrderByNewsDatetimeDesc();
}