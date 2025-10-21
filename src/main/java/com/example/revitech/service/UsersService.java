package com.example.revitech.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    /**
     * 【復活させたメソッド】
     * すべてのユーザーを取得します。
     */
    public List<Users> findAll() {
        return usersRepository.findAll();
    }

    /**
     * 【復活させたメソッド】
     * メールアドレスでユーザーを検索します。
     */
    public Optional<Users> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }
    
    /**
     * 【復活させたメソッド】
     * 名前でユーザーを検索します。（ログイン機能で使用）
     */
    public Optional<Users> findByName(String name) {
        return usersRepository.findByName(name);
    }

    /**
     * 【復活させたメソッド】
     * IDでユーザーを検索します。
     */
    public Optional<Users> findById(Integer id) {
        return usersRepository.findById(id);
    }

    /**
     * 【復活させたメソッド】
     * 役割（role）でユーザーを検索します。（教員一覧の元データ取得などで使用）
     */
    public List<Users> findByRole(Integer role) {
        return usersRepository.findByRole(role);
    }

    /**
     * 【復活させたメソッド】
     * メールアドレスが既に使用されているかチェックします。
     */
    public boolean isEmailTaken(String email) {
        return usersRepository.findByEmail(email).isPresent();
    }

    /**
     * 【復活させたメソッド】
     * ユーザー情報を保存（新規登録・更新）します。パスワードは自動的にハッシュ化されます。
     */
    public Users save(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

    /**
     * 【復活させたメソッド】
     * キーワードでユーザーを部分一致検索します。
     */
    public List<Users> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        return usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }

    /**
     * 【新規追加したメソッド】
     * 教員一覧ページに表示するためのDTOリスト（ダミーデータ版）を取得します。
     */
    public List<TeacherListDto> getTeacherListDetails() {
        // --- ここからダミーデータ生成処理 ---
        List<TeacherListDto> dummyList = new ArrayList<>();

        dummyList.add(new TeacherListDto(101, "田中 健", "tanaka@example.com", List.of("Java", "データベース"), 4.5));
        dummyList.add(new TeacherListDto(102, "鈴木 あやか", "suzuki@example.com", List.of("Webデザイン", "UI/UX"), 4.8));
        dummyList.add(new TeacherListDto(103, "佐藤 浩一", "sato@example.com", List.of("ネットワーク", "セキュリティ"), 3.2));
        dummyList.add(new TeacherListDto(104, "高橋 まり子", "takahashi@example.com", List.of("アルゴリズム"), 4.0));
        dummyList.add(new TeacherListDto(105, "伊藤 雄大", "ito@example.com", List.of("プロジェクト管理"), 3.8));

        return dummyList;
        // --- ダミーデータ生成ここまで ---
    }
}