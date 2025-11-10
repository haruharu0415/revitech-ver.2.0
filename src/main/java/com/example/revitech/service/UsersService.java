package com.example.revitech.service;

import java.util.List;
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

    public List<TeacherListDto> getTeacherListDetails() {
        List<Users> teachers = findByRole(2);

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

            // ★★★ ここを修正 ★★★
            // エラーの原因である平均スコアの計算を停止し、仮のスコアを渡します。
            Double avgScore = 0.0;
            // Double avgScore = teacherReviewRepository.findAverageScoreByTeacherId(teacher.getUsersId()); // ← この行をコメントアウト

            return new TeacherListDto(
                teacher.getUsersId(),
                teacher.getName(),
                teacher.getEmail(),
                subjects,
                avgScore
            );
        }).collect(Collectors.toList());
    }
    
    // --- 以下の既存メソッドは変更ありません ---

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