package com.example.revitech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.revitech.entity.Teacher;
import com.example.revitech.service.TeacherService;

@Controller
public class TeacherController {
	 private final TeacherService teacherService;

	    @Autowired
	    public TeacherController(TeacherService teacherService) {
	        this.teacherService = teacherService;
	    }

	    @GetMapping("/home")
	    public String getHome(Model model) {
	        var homes = teacherService.findAll();
	        model.addAttribute("home",homes);
	        return "home";
	    }
	    
	    @GetMapping("/teacher-list")
	    public String showTeacherList() {
	        return "teacher-list"; 
	    }
	    
	    @GetMapping("/terms")
	    public String terms() {
	        return "terms"; 
	    }

	    // ======================================================
	    // ★★★ このメソッドを新規追加 ★★★
	    // サンプル用のプロフィールページを表示するためのメソッド
	    // ======================================================
	    @GetMapping("/teacher-profile")
	    public String showSampleProfile() {
	        return "teacher-profile";
	    }
	    // ======================================================
	    
	    // 以下のメソッドはDB連携用ですが、他の機能のために残しておきます
	    @GetMapping("/teachers/{id}")
	    public String showTeacherProfile(@PathVariable("id") Integer id, Model model) {
	        Teacher teacher = teacherService.findById(id)
	                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher Id:" + id));
	        model.addAttribute("teacher", teacher);
	        return "teacher-profile";
	    }

	    @GetMapping("/teachers/new")
	    public String showCreationForm(Model model) {
	        model.addAttribute("teacher", new Teacher());
	        return "teacher-form";
	    }

	    @PostMapping("/teachers")
	    public String createTeacher(@ModelAttribute Teacher teacher) {
	        teacher.setRole("TEACHER");
	        teacherService.save(teacher);
	        return "redirect:/teacher-list";
	    }
}