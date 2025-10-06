package com.example.revitech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.revitech.service.TeacherService;
//commitsimasune
@Controller
public class TeacherController {
	 private final TeacherService teacherService;

	    @Autowired
	    public TeacherController(TeacherService teacherService) {
	        this.teacherService = teacherService;
	    }
//dddd
	    @GetMapping("/home")
	    public String getArtists(Model model) {
	        var homes = teacherService.findAll();
	        model.addAttribute("home",homes);
	        return "home";
	    }
	    @GetMapping("/teacher-list")
	    public String teacherList() {
	        return "teacher-list"; 
	    }
	    @GetMapping("/terms")
	    public String terms() {
	        return "terms"; 
	    }
	    
	    /*@GetMapping("/teacher-list/review")
	    public String showReview() {
	    	return "review";
	    }*/
}
