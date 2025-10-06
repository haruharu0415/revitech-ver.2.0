/*package com.example.revitech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.revitech.service.TeacherService;

@Controller
public class HomeController {
	
	private TeacherService teacherService;

	@Autowired
    public HomeController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }
	
	@GetMapping("/home")
	public String homeView() {
		return "home";
	}
	
	@GetMapping("/group")
	public String groupView() {
		return "group";
	}
	
	@GetMapping("/dm")
	public String dmView() {
		return "dm";
	}
	
	
	
}*/
