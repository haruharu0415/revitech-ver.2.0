package com.example.revitech.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.revitech.entity.Teacher;
import com.example.revitech.repository.TeacherRepository;

@Service
public class TeacherService {
	 private final TeacherRepository teacherRepository;

	    @Autowired
	    public TeacherService(TeacherRepository teacherRepository) {
	        this.teacherRepository = teacherRepository;
	    }

	    public List<Teacher> findAll() {
	        return teacherRepository.findAll();
	    }
//111

}
