// fileName: SubjectSelectController.java
package com.example.revitech.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.entity.Enrollment;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.EnrollmentRepository;
import com.example.revitech.repository.SubjectRepository;
import com.example.revitech.service.UsersService;

@Controller
public class SubjectSelectController {

    private final SubjectRepository subjectRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UsersService usersService;

    public SubjectSelectController(SubjectRepository subjectRepository, EnrollmentRepository enrollmentRepository, UsersService usersService) {
        this.subjectRepository = subjectRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.usersService = usersService;
    }

    /**
     * 学科選択ページを表示します。
     */
    @GetMapping("/subject-select")
    public String showSubjectSelectPage(@RequestParam("userId") Integer userId, Model model) {
        Optional<Users> userOpt = usersService.findById(userId);

        if (userOpt.isEmpty() || userOpt.get().getRole() != 1) {
            // ユーザーが存在しない、または生徒でない場合はログイン画面へ
            return "redirect:/login";
        }
        
        model.addAttribute("userId", userId);
        // 全学科リストをモデルに追加
        model.addAttribute("subjects", subjectRepository.findAll());
        
        return "subject-select"; // templates/subject-select.html を表示
    }

    /**
     * 学科選択を処理し、Enrollmentに保存します。
     */
    @PostMapping("/subject-select")
    public String processSubjectSelect(@RequestParam("userId") Integer userId,
                                       @RequestParam("subjectId") Integer subjectId,
                                       RedirectAttributes redirectAttributes) {
        
        Optional<Users> userOpt = usersService.findById(userId);

        if (userOpt.isEmpty() || userOpt.get().getRole() != 1) {
            redirectAttributes.addFlashAttribute("error", "不正な操作です。");
            return "redirect:/login";
        }
        
        // Enrollment エンティティを利用して、ユーザーIDと学科IDを保存
        Enrollment enrollment = new Enrollment();
        enrollment.setUsersId(userId);
        enrollment.setSubjectId(subjectId); 
        
        enrollmentRepository.save(enrollment);
        
        // 全ての登録が完了したので、ログイン画面へリダイレクト
        redirectAttributes.addFlashAttribute("message", userOpt.get().getName() + "様の登録が完了しました。ログインしてください。");
        return "redirect:/login";
    }
}