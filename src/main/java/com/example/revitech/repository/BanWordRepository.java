package com.example.revitech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.News;

@Repository
public interface BanWordRepository extends JpaRepository<News, Integer> {



}