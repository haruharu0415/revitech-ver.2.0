package com.example.revitech.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.dto.TeacherListDto;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.TeacherReviewRepository;
import com.example.revitech.repository.UsersRepository;

@Service
@Transactional
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeacherReviewRepository teacherReviewRepository;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, TeacherReviewRepository teacherReviewRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.teacherReviewRepository = teacherReviewRepository;
    }

    // --- 検索機能 (ここが修正ポイント) ---
    /**
     * 名前またはメールアドレスでユーザーを曖昧検索します。
     */
    public List<Users> searchUsers(String keyword) {
        // キーワードが空の場合は空リストを返す（全件表示防止）
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        String query = keyword.trim();
        // 名前 OR メールアドレス で部分一致検索
        return usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
    }

    // --- 教員一覧取得ロジック ---
    public List<TeacherListDto> getTeacherListDetails() {
        List<Users> teachers = findByRole(2); // Role 2 = 教員

        return teachers.stream().map(teacher -> {
            List<String> subjects;
            // 仮の担当科目ロジック
            switch (teacher.getName()) {
                case "福井先生": subjects = List.of("ハードウェア", "卒業制作"); break;
                case "佐藤先生": subjects = List.of("Java"); break;
                case "柴田先生": subjects = List.of("JavaScript", "PHP"); break;
                case "河野先生": subjects = List.of("アルゴリズム"); break;
                case "小宮山先生": subjects = List.of("ソフトウェア"); break;
                default: subjects = List.of("担当科目"); break;
            }
            // 仮の平均スコア（本来はDBから算出）
            Double avgScore = 0.0;
            return new TeacherListDto(
                teacher.getUsersId(),
                teacher.getName(),
                teacher.getEmail(),
                subjects,
                avgScore
            );
        }).collect(Collectors.toList());
    }
    
    // --- 生徒を学科別にグループ化 ---
    @Transactional(readOnly = true)
    public Map<String, List<Users>> findAllStudentsGroupedBySubject() {
        List<Users> students = findByRole(1); // Role 1 = 生徒
        Map<String, List<Users>> groupedMap = new LinkedHashMap<>(); 
        
        groupedMap.put("情報処理科", new ArrayList<>());
        groupedMap.put("高度情報処理科", new ArrayList<>());
        
        for (Users student : students) {
            // 仮の学科判定ロジック（IDの偶奇）
            String subjectName;
            if (student.getUsersId() % 2 == 0) {
                subjectName = "情報処理科";
            } else {
                subjectName = "高度情報処理科";
            }
            groupedMap.computeIfAbsent(subjectName, k -> new ArrayList<>()).add(student);
        }
        return groupedMap;
    }

    // --- 基本CRUDメソッド ---
    public List<Users> findAll() { return usersRepository.findAll(); }
    public Optional<Users> findByEmail(String email) { return usersRepository.findByEmail(email); }
    public Optional<Users> findByName(String name) { return usersRepository.findByName(name); }
    public Optional<Users> findById(Integer id) { return usersRepository.findById(id); }
    public List<Users> findByRole(Integer role) { return usersRepository.findByRole(role); }
    public boolean isEmailTaken(String email) { return usersRepository.findByEmail(email).isPresent(); }

    public Users save(Users user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return usersRepository.save(user);
    }
    
    public Users saveRawUser(Users user) {
        return usersRepository.save(user);
    }
}