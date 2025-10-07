package com.example.revitech.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.revitech.entity.Users;
import com.example.revitech.repository.UsersRepository;

@Service

public class UsersService {
	private final UsersRepository usersRepository ;
	@Autowired
	public UsersService(UsersRepository usersRepository) {
		this.usersRepository = usersRepository;
	}
	
	public List<Users>findALL(){
	return usersRepository.findALL();
	}
	
	public void save(Users users,MultipartFile file) throws I0Exeptione{
		String files = "static/image/"+file.getOriginalFilename();
		Files.write(Paths.get(files), files.getBytes());
		
	}

}
