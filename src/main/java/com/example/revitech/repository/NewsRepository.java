package com.example.revitech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.News;

@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {
    // findByNameは不要、またはNewsIdで検索したいなら以下のように変更（findAll()で全件取得できるため、特別な検索がなければ不要）
    // Optional<News> findByNewsId(Integer newsId); 
}