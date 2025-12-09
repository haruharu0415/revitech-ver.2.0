package com.example.revitech.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public List<News> findAllNews() {
        List<News> list = newsRepository.findAll();
        // 日付の新しい順（降順）にソート
        list.sort(Comparator.comparing(News::getNewsDatetime).reversed());
        return list;
    }

    /**
     * ユーザー閲覧可能なお知らせを取得（日付降順）
     */
    public List<News> findNewsForUser(Users user) {
        if (user == null) {
            return Collections.emptyList();
        }

        List<News> resultList;

        Integer role = user.getRole();
        // roleがnullの場合の安全策
        boolean isAdmin = (role != null) && (role == 2 || role == 9 || role == 3);

        if (isAdmin) {
            resultList = newsRepository.findAll();
        } else {
            List<News> allNews = newsRepository.findAll();
            List<News> visibleNews = new ArrayList<>();
            Integer userId = user.getUsersId();

            for (News news : allNews) {
                List<Integer> recipients = news.getRecipientUserIds();
                
                // 受信者リストが空＝全員向け、または自分宛て
                boolean isForEveryone = (recipients == null || recipients.isEmpty());
                boolean isForMe = (recipients != null && recipients.contains(userId));

                if (isForEveryone || isForMe) {
                    visibleNews.add(news);
                }
            }
            resultList = visibleNews;
        }

        // 日付降順ソート
        if (resultList != null && !resultList.isEmpty()) {
            resultList.sort((a, b) -> {
                if (b.getNewsDatetime() == null || a.getNewsDatetime() == null) return 0;
                return b.getNewsDatetime().compareTo(a.getNewsDatetime());
            });
        }

        return resultList;
    }

    /**
     * ★★★ トップページ用：最新のN件のみを取得 ★★★
     */
    public List<News> findTopNewsForUser(Users user, int limit) {
        List<News> allVisible = findNewsForUser(user);
        
        if (allVisible == null || allVisible.isEmpty()) {
            return Collections.emptyList();
        }

        return allVisible.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Optional<News> findById(Integer id) {
        return newsRepository.findById(id);
    }

    public void createNews(News news) {
        newsRepository.save(news);
    }

    public void deleteNews(Integer id) {
        newsRepository.deleteById(id);
    }
}