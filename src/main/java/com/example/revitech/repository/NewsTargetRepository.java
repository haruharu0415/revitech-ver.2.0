package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.NewsTarget;

@Repository
public interface NewsTargetRepository extends JpaRepository<NewsTarget, Integer> {
    
    // お知らせIDに紐づく受信対象ユーザーIDのリストを取得
    @Query("SELECT nt.userId FROM NewsTarget nt WHERE nt.newsId = :newsId")
    List<Integer> findUserIdsByNewsId(Integer newsId);
    
    // お知らせ削除時にターゲット情報も消す
    void deleteByNewsId(Integer newsId);
}