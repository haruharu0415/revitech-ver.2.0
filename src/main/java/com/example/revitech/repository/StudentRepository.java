package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.Student;

@Repository
public interface StudentRepository extends CrudRepository<Student, Integer>{
	public List<Student> findAll();
	
}
