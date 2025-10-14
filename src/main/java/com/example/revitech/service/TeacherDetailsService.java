package com.example.revitech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.revitech.entity.Teacher;
import com.example.revitech.repository.TeacherRepository;

@Service
public class TeacherDetailsService implements UserDetailsService {

    private final TeacherRepository teacherRepository;

    @Autowired
    public TeacherDetailsService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Teacher teacher = teacherRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("ユーザーが存在しません: " + username));

        return User.builder()
                .username(teacher.getUsername())
                .password(teacher.getPassword())
                .roles(teacher.getRole()) // 例: USER
                .build();
    }
}
