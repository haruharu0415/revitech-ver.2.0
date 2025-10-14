// TeacherRepository.java
package com.example.revitech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.Teacher;

@Repository
public interface TeacherRepository extends CrudRepository<Teacher, Integer> {
    List<Teacher> findAll();
    Optional<Teacher> findByUsername(String username);
}
