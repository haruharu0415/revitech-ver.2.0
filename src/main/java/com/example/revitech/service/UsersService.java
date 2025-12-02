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

    // 教員一覧取得用（変更なし）
    public List<TeacherListDto> getTeacherListDetails() {
        List<Users> teachers = findByRole(2); // Role 2 = 教員

        return teachers.stream().map(teacher -> {
            List<String> subjects;
            switch (teacher.getName()) {
                case "福井先生": subjects = List.of("ハードウェア", "卒業制作"); break;
                case "佐藤先生": subjects = List.of("Java"); break;
                case "柴田先生": subjects = List.of("JavaScript", "PHP"); break;
                case "河野先生": subjects = List.of("アルゴリズム"); break;
                case "小宮山先生": subjects = List.of("ソフトウェア"); break;
                default: subjects = List.of("担当科目"); break;
            }
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
    
    // 【★★ 新規追加メソッド: 生徒を学科別にグループ化 ★★】
    @Transactional(readOnly = true)
    public Map<String, List<Users>> findAllStudentsGroupedBySubject() {
        
        // Role 1 = 生徒 を取得
        List<Users> students = findByRole(1);
        
        // 学科名でグループ化するためのマップ (ソート順を保持するためLinkedHashMapを使用)
        Map<String, List<Users>> groupedMap = new LinkedHashMap<>(); 
        
        // データがない場合のカテゴリを用意（空でも表示順序を確保したい場合など）
        groupedMap.put("情報処理科", new ArrayList<>());
        groupedMap.put("高度情報処理科", new ArrayList<>());
        
        for (Users student : students) {
            // ★ここに「生徒がどの学科に属するか」を判定するロジックが入ります。
            // 現状はDBに関連付けがないため、仮にIDが偶数の人を「情報処理科」、奇数を「高度情報処理科」としています。
            // 実際には enrollments テーブルなどを結合して判定してください。
            String subjectName;
            if (student.getUsersId() % 2 == 0) {
                subjectName = "情報処理科";
            } else {
                subjectName = "高度情報処理科";
            }
            
            // マップに追加
            groupedMap.computeIfAbsent(subjectName, k -> new ArrayList<>()).add(student);
        }
        
        // 生徒がいない空のリストを除去したり、キーでソートしたりして返す
        return groupedMap;
    }

    // --- 既存メソッド ---
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
    
    public List<Users> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) { return List.of(); }
        return usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }
}