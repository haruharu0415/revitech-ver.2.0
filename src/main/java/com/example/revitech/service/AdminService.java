package com.example.revitech.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.revitech.entity.Admin;
import com.example.revitech.repository.AdminRepository;

@Service
public class AdminService {
	private final AdminRepository adminRepository ;
	@Autowired
	public AdminService(AdminRepository adminRepository) {
		this.adminRepository = adminRepository;
		
	}
	public List<Admin>findAll(){
		
		return adminRepository.findALL();
		
	}

}
