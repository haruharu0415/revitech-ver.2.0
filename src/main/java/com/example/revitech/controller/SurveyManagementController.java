package com.example.revitech.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.dto.TeacherListDto;
import com.example.revitech.entity.Survey;
import com.example.revitech.entity.Users;
import com.example.revitech.service.SurveyService;
import com.example.revitech.service.UsersService;

@Controller
@RequestMapping("/teacher/survey")
public class SurveyManagementController {

    private final SurveyService surveyService;
    private final UsersService usersService;

    public SurveyManagementController(SurveyService surveyService, UsersService usersService) {
        this.surveyService = surveyService;
        this.usersService = usersService;
    }

    // アンケート作成画面表示
    @GetMapping("/create")
    public String showCreateForm(Model model, @AuthenticationPrincipal User loginUser) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        if (currentUser.getRole() != 2 && currentUser.getRole() != 3) {
            return "redirect:/home";
        }

        // ★★★ 修正: 引数に null を渡して全教員リストを取得 ★★★
        // UsersService側の変更に合わせて引数が必要になりました
        List<TeacherListDto> teachers = usersService.getTeacherListDetails(null);
        model.addAttribute("teachers", teachers);

        model.addAttribute("subjectUserMap", usersService.findAllStudentsGroupedBySubject());
        return "survey-create";
    }

    // アンケート保存処理
    @PostMapping("/create")
    public String createSurvey(@RequestParam("title") String title,
                               @RequestParam("targetTeacherId") Integer targetTeacherId,
                               @RequestParam("questionBody") List<String> questionBodies,
                               @RequestParam(value = "targetUserIds", required = false) List<Integer> targetUserIds,
                               @AuthenticationPrincipal User loginUser,
                               RedirectAttributes redirectAttributes) {
        
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        if (currentUser.getRole() != 2 && currentUser.getRole() != 3) {
            return "redirect:/home";
        }
        
        surveyService.createSurvey(title, currentUser.getUsersId(), targetTeacherId, questionBodies, targetUserIds);
        
        redirectAttributes.addFlashAttribute("successMessage", "アンケート「" + title + "」を作成しました。");
        return "redirect:/teacher/survey/list"; 
    }
    
    // アンケート一覧表示
    @GetMapping("/list")
    public String listSurveys(Model model, @AuthenticationPrincipal User loginUser) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        if (currentUser.getRole() != 2 && currentUser.getRole() != 3) {
            return "redirect:/home";
        }

        List<Survey> surveys = surveyService.getSurveysByTeacher(currentUser.getUsersId());
        model.addAttribute("surveys", surveys);
        
        return "teacher-survey-list";
    }

    // アンケート削除
    @PostMapping("/delete/{surveyId}")
    public String deleteSurvey(@PathVariable("surveyId") Integer surveyId,
                               @AuthenticationPrincipal User loginUser,
                               RedirectAttributes redirectAttributes) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        if (currentUser.getRole() != 2 && currentUser.getRole() != 3) {
            return "redirect:/home";
        }
        
        try {
            surveyService.deleteSurvey(surveyId);
            redirectAttributes.addFlashAttribute("successMessage", "アンケートを削除しました。集計結果からも除外されました。");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "削除中にエラーが発生しました。");
        }

        return "redirect:/teacher/survey/list";
    }
}