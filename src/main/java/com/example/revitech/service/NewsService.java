package com.example.revitech.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.entity.News;
import com.example.revitech.repository.NewsRepository;

@Service // @Serviceアノテーションを追加
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    /**
     * お知らせを登録する（教員側）
     * @param news 登録するお知らせのEntity（IDはnullで、登録日時などがセット済み）
     * @return 登録されたNews Entity
     */
    @Transactional // データベース変更操作にはトランザクションを付与
    public News createNews(News news) {
        // Newsオブジェクトを受け取り、Repository経由でデータベースに保存
        return newsRepository.save(news);
    }

    /**
     * 全てのお知らせを取得する（生徒側）
     * @return 全てのNews Entityのリスト
     */
    @Transactional(readOnly = true) // 読み取り専用のトランザクション
    public List<News> findAllNews() {
        // データベースから全てのお知らせを取得
        return newsRepository.findAll();
    }
    
    // newsText(Integer id)のロジックはNewsIdによる個別検索なので、残しても良い
    // public Optional<News> newsText(Integer id ) {
    //     return newsRepository.findById(id);
    // }
}