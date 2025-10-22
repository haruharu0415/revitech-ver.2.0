package com.example.revitech.controller;

import java.util.Optional; // Optional をインポート

import org.springframework.beans.factory.annotation.Autowired; // Autowired をインポート
// SecurityContextHolder と Authentication をインポート
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Model をインポート
import org.springframework.web.bind.annotation.GetMapping;

import com.example.revitech.dto.UserProfileDto; // ★ UserProfileDto をインポート
// Profile 関連の Entity, Repository, DTO をインポート
import com.example.revitech.entity.StudentProfile;
import com.example.revitech.entity.TeacherProfile;
// Users と UsersService をインポート
import com.example.revitech.entity.Users;
import com.example.revitech.repository.StudentProfileRepository;
import com.example.revitech.repository.TeacherProfileRepository;
import com.example.revitech.service.UsersService;

@Controller
public class UserController {

    @Autowired
    private UsersService usersService;

    // ★ プロフィールリポジトリをインジェクション ★
    @Autowired
    private TeacherProfileRepository teacherProfileRepository;
    @Autowired
    private StudentProfileRepository studentProfileRepository;

    // 利用規約ページ
    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }

    // グループ一覧ページ (JavaScript で内容を読み込む想定)
    @GetMapping("/group")
    public String group() {
        return "group";
    }

    // グループ作成ページ
    @GetMapping("/group-create")
    public String groupCreate(Model model) {
        // ログインユーザーIDを Model に追加
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());
        if (userOpt.isPresent()) {
            model.addAttribute("userId", userOpt.get().getId());
        } else {
            return "redirect:/login"; // ログインページへ
        }
        return "group-create";
    }

    // 教員一覧ページ (内容は Thymeleaf で表示する想定)
    @GetMapping("/teacher-list")
    public String showTeacherList(Model model) {
        // ★ 必要に応じて教員リストを取得して Model に追加 ★
        // List<Users> teachers = usersService.findUsersByRole(2); // 例: Role=2 が教員の場合
        // model.addAttribute("teachers", teachers);
        return "teacher-list";
    }

    // オプションページ
    @GetMapping("/option")
    public String option(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());

        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            // ★ アイコンURLを取得 ★
            String iconUrl = findIconUrl(user.getId(), user.getRole());
            // ★ UserProfileDto を作成して Model に追加 ★
            UserProfileDto userDto = new UserProfileDto(user.getId(), user.getName(), iconUrl);
            model.addAttribute("user", userDto); // "user" という名前で渡す
        }
        // ログインしていなければ Security Config で /login にリダイレクトされるはず
        return "option";
    }

    // プロフィール編集ページ (フォーム表示)
    @GetMapping("/profile/edit")
    public String profileEditForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());

        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            // ★ アイコンURLを取得 ★
            String iconUrl = findIconUrl(user.getId(), user.getRole());
             // ★ UserProfileDto を作成して Model に追加 ★
            UserProfileDto userDto = new UserProfileDto(user.getId(), user.getName(), iconUrl);
            model.addAttribute("user", userDto); // "user" という名前で渡す
        } else {
            return "redirect:/login"; // ログインページへ
        }
        return "edit_profile";
    }

    // ★★★ ユーザーIDと役割に基づいてアイコンURLを取得するヘルパーメソッド ★★★
    private String findIconUrl(Long userId, Integer role) {
        String iconUrl = null;
        // role の値は実際の定義に合わせる (例: 2=Teacher, 3=Student)
        if (role != null) {
            if (role == 2) { // 教員の場合
                // TeacherProfile を検索 (主キーは userId)
                Optional<TeacherProfile> profileOpt = teacherProfileRepository.findById(userId);
                if (profileOpt.isPresent()) {
                    // TeacherProfile の icon_picture カラムに対応する Getter を呼び出す
                    iconUrl = profileOpt.get().getIconPicture();
                }
            } else if (role == 3) { // 学生の場合
                // StudentProfile を検索 (主キーは userId)
                Optional<StudentProfile> profileOpt = studentProfileRepository.findById(userId);
                if (profileOpt.isPresent()) {
                    // StudentProfile の icon_picture カラムに対応する Getter を呼び出す
                    iconUrl = profileOpt.get().getIconPicture();
                }
            }
        }
        // アイコンが見つからない場合は null を返す (HTML側でデフォルト画像を表示)
        return iconUrl;
    }
    // ★★★ ヘルパーメソッドここまで ★★★

    // (注意: プロフィール更新処理 @PostMapping("/profile/edit") は別途実装が必要です)
    // このメソッドでは、フォームから name や iconFile を受け取り、
    // Users と TeacherProfile/StudentProfile の両方を更新する必要があります。

}