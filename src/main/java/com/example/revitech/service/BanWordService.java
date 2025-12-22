package com.example.revitech.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.entity.BanWord;
import com.example.revitech.repository.BanWordRepository;

@Service
@Transactional
public class BanWordService {

    private final BanWordRepository repository;

    public BanWordService(BanWordRepository repository) {
        this.repository = repository;
    }

    public List<BanWord> getBanWords(Integer teacherId) {
        return repository.findByTeacherId(teacherId);
    }

    public void addBanWord(Integer teacherId, String word) {
        if (word == null || word.trim().isEmpty()) return;
        String trimWord = word.trim();
        
        if (!repository.existsByTeacherIdAndWord(teacherId, trimWord)) {
            BanWord bw = new BanWord();
            bw.setTeacherId(teacherId);
            bw.setWord(trimWord);
            repository.save(bw);
        }
    }

    public void deleteBanWord(Integer banId) {
        repository.deleteById(banId);
    }
    
    public BanWord findById(Integer banId) {
        return repository.findById(banId).orElse(null);
    }

    public boolean containsBanWord(Integer teacherId, String comment) {
        if (comment == null || comment.isEmpty()) return false;
        
        List<BanWord> banWords = repository.findByTeacherId(teacherId);
        
        for (BanWord bw : banWords) {
            if (comment.contains(bw.getWord())) {
                return true;
            }
        }
        return false;
    }
}