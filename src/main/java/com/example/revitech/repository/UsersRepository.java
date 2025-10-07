package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.Users;


@Repository
public interface UsersRepository extends CrudRepository<Users, Integer> {
public List<Users>findALL();
}
