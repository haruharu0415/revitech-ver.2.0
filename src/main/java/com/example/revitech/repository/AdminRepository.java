package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.Admin;

@Repository
public interface AdminRepository extends CrudRepository<Admin,Integer>{
public List<Admin> findALL();
}
