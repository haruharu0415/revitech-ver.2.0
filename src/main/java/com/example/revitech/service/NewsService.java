package com.example.revitech.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Optional<News> findById(Integer id) {
        return newsRepository.findById(id);
    }

    // ★★★ 修正: 作成時にリストをカンマ区切り文字列に変換して保存 ★★★
    public void createNews(News news) {
        // フォームから受け取った recipientUserIds (List<Integer>) を
        // recipientIds (String "1,2,3") に変換
        List<Integer> idList = news.getRecipientUserIds();
        if (idList != null && !idList.isEmpty()) {
            // nullを除外してカンマで結合
            String csv = idList.stream()
                .filter(id -> id != null)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
            news.setRecipientIds(csv);
        } else {
            news.setRecipientIds(null); // 全員対象
        }

        newsRepository.save(news);
    }
    
    // エイリアス
    public void saveNews(News news) {
        createNews(news);
    }

    public void deleteNews(Integer id) {
        newsRepository.deleteById(id);
    }

    // トップニュース取得
    public List<News> getTopNews(int limit) {
        return newsRepository.findTop3ByOrderByNewsDatetimeDesc();
    }

    // ★★★ 修正: 文字列(CSV)を解析してフィルタリング ★★★
    public List<News> findNewsForUser(Users user) {
        List<News> allNews = findAllNews();
        
        // ログインしていない場合は「全体公開（recipientIdsが空）」のみ
        if (user == null) {
            return filterPublicNews(allNews);
        }

        List<News> visibleNews = new ArrayList<>();
        Integer userId = user.getUsersId();
        Integer userRole = user.getRole();

        for (News news : allNews) {
            String targetCsv = news.getRecipientIds();

            // 1. 指定がない(nullまたは空文字)なら全員宛て
            if (targetCsv == null || targetCsv.trim().isEmpty()) {
                visibleNews.add(news);
                continue;
            }

            // 2. 指定がある場合
            //  - 管理者(3)は全部見える
            //  - 送信者は自分の投稿が見える
            boolean isAdmin = (userRole != null && userRole == 3);
            boolean isSender = (news.getSenderId() != null && news.getSenderId().equals(userId));

            if (isAdmin || isSender) {
                visibleNews.add(news);
                continue;
            }

            // 3. カンマ区切りを分解して、自分のIDが含まれているかチェック
            // 例: "69,109,114" -> ["69", "109", "114"]
            String[] targetIds = targetCsv.split(",");
            boolean isTarget = false;
            String myIdStr = String.valueOf(userId);

            for (String tid : targetIds) {
                if (tid.trim().equals(myIdStr)) {
                    isTarget = true;
                    break;
                }
            }

            if (isTarget) {
                visibleNews.add(news);
            }
        }
        
        return visibleNews;
    }

    private List<News> filterPublicNews(List<News> newsList) {
        List<News> publicNews = new ArrayList<>();
        for (News news : newsList) {
            String targetCsv = news.getRecipientIds();
            if (targetCsv == null || targetCsv.trim().isEmpty()) {
                publicNews.add(news);
            }
        }
        return publicNews;
    }
}