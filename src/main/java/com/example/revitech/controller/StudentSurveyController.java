package com.example.revitech.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.entity.Question;
import com.example.revitech.entity.Survey;
import com.example.revitech.entity.Users;
import com.example.revitech.form.ReviewForm;
import com.example.revitech.service.SurveyService;
import com.example.revitech.service.UsersService;

@Controller
@RequestMapping("/student/survey")
public class StudentSurveyController {

    private final SurveyService surveyService;
    private final UsersService usersService;

    public StudentSurveyController(SurveyService surveyService, UsersService usersService) {
        this.surveyService = surveyService;
        this.usersService = usersService;
    }

    // 生徒用アンケート一覧 (省略)
    @GetMapping("/list")
    public String listSurveys(Model model, @AuthenticationPrincipal User loginUser) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        
        List<Survey> surveys = surveyService.getSurveysForStudent(currentUser.getUsersId());
        model.addAttribute("surveys", surveys);
        
        return "student-survey-list";
    }

    // 回答フォーム表示
    @GetMapping("/answer/{surveyId}")
    public String showAnswerForm(@PathVariable Integer surveyId, Model model, @AuthenticationPrincipal User loginUser, RedirectAttributes redirectAttributes) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();

        if (surveyService.hasStudentAnswered(surveyId, currentUser.getUsersId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "このアンケートは解答済みです");
            return "redirect:/student/survey/list";
        }

        Survey survey = surveyService.findSurveyById(surveyId).orElseThrow(() -> new RuntimeException("Survey not found"));
        
        // ★★★ 修正: 先生IDを Survey の targetTeacherId から取得 ★★★
        Integer targetTeacherId = survey.getTargetTeacherId();
        Users teacher = usersService.findById(targetTeacherId).orElseThrow(() -> new RuntimeException("Target Teacher not found"));
        
        List<Question> questions = surveyService.getQuestionsBySurveyId(surveyId);

        model.addAttribute("survey", survey);
        model.addAttribute("teacher", teacher);
        model.addAttribute("questions", questions);

        ReviewForm form = new ReviewForm();
        form.setSurveyId(surveyId);
        form.setTeacherId(targetTeacherId); // ReviewForm に targetTeacherId をセット
        model.addAttribute("reviewForm", form);

        return "review-form";
    }

    // 回答送信処理 (省略)
    @PostMapping("/submit")
    public String submitAnswer(@ModelAttribute ReviewForm reviewForm,
                               @AuthenticationPrincipal User loginUser,
                               RedirectAttributes redirectAttributes) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        
        try {
            surveyService.saveSurveyResponse(currentUser.getUsersId(), reviewForm);
            redirectAttributes.addFlashAttribute("successMessage", "回答を送信しました！ご協力ありがとうございます。");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "既に回答済みのアンケートです。");
        }
        
        return "redirect:/student/survey/list";
    }
}