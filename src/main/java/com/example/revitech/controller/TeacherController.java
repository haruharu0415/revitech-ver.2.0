package com.example.revitech.controller;

import java.util.List; // Listをインポート

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // PathVariableをインポート

import com.example.revitech.entity.Teacher; // Teacherをインポート
import com.example.revitech.service.TeacherService;

@Controller
public class TeacherController {
    private final TeacherService teacherService;

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/home")
    public String getHome(Model model) { // メソッド名を変更しました (getArtists -> getHome)
        // ここはhomeのデータを取得するロジックかと思いますので、一旦そのままにしています
        var homes = teacherService.findAll();
        model.addAttribute("home", homes);
        return "home";
    }

    // ======================================================
    // ★★★ ここから変更 ★★★
    // ======================================================
    @GetMapping("/teacher-list")
    public String showTeacherList(Model model) { // modelを引数に追加
        // 1. TeacherServiceを使って、全先生のリストを取得
        List<Teacher> teachers = teacherService.findAll();
        // 2. Modelに "teachers" という名前でリストを詰める
        model.addAttribute("teachers", teachers);
        // 3. Thymeleafが処理する "teacher-list.html" を返す
        return "teacher-list";
    }

    // プロフィールページ用のメソッドを新規追加
    // URLが /teachers/1 や /teachers/5 のように変化するのに対応
    @GetMapping("/teachers/{id}")
    public String showTeacherProfile(@PathVariable("id") Integer id, Model model) {
        // 1. URLのidを使って、先生を1人だけ探す
        Teacher teacher = teacherService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher Id:" + id));
        
        // 2. 見つかった先生の情報をModelに詰める
        model.addAttribute("teacher", teacher);
        
        // 3. "teacher-profile.html" を表示する
        return "teacher-profile";
    }
    // ======================================================
    // ★★★ 変更ここまで ★★★
    // ======================================================

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }
}