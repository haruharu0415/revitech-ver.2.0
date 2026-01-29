package com.example.revitech.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.entity.News;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.NewsRepository;

@Service
@Transactional
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    // 全件取得 (新しい順)
    public List<News> findAllNews() {
        return newsRepository.findAllByOrderByNewsDatetimeDesc();
    }

    // IDで取得
    public Optional<News> findById(Integer id) {
        return newsRepository.findById(id);
    }

    // 保存 (作成・更新)
    // Controllerで createNews と呼んでいるのでエイリアスも作成
    public void saveNews(News news) {
        newsRepository.save(news);
    }
    
    public void createNews(News news) {
        saveNews(news);
    }

    // 削除
    public void deleteNews(Integer id) {
        newsRepository.deleteById(id);
    }

    // トップニュース取得 (HomeController用)
    public List<News> getTopNews(int limit) {
        return newsRepository.findTop3ByOrderByNewsDatetimeDesc();
    }

    // ★★★ 追加: ユーザー別ニュース取得 (NewsController用) ★★★
    public List<News> findNewsForUser(Users user) {
        // 現状は全ユーザーに全ニュースを表示する仕様とします。
        // もし「特定の学科（SubjectId）のみ」という機能がある場合は、
        // ここで user.getEnrollment().getSubjectId() などを使ってフィルタリングします。
        
        return findAllNews();
    }
}