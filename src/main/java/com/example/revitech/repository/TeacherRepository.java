package com.example.revitech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.Teacher;

@Repository
public interface TeacherRepository extends CrudRepository<Teacher, Integer> {
    public List<Teacher> findAll();
    public Optional<Teacher> findByName(String name);
}