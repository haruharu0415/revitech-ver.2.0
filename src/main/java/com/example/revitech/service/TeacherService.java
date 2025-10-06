package com.example.revitech.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.revitech.entity.Teacher;
import com.example.revitech.repository.TeacherRepository;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository, PasswordEncoder passwordEncoder) {
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    // ======================================================
    // ★★★ このメソッドを新規追加 ★★★
    // ======================================================
    public Optional<Teacher> findById(Integer id) {
        return teacherRepository.findById(id);
    }
    // ======================================================

    public Teacher save(Teacher teacher) {
        teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        return teacherRepository.save(teacher);
    }

    public boolean isUsernameTaken(String username) {
        return teacherRepository.findByUsername(username).isPresent();
    }

    public Optional<Teacher> findByUsername(String username) {
        return teacherRepository.findByUsername(username);
    }
}