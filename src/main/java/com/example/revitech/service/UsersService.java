package com.example.revitech.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 【追加】トランザクション管理

import com.example.revitech.entity.Users;
import com.example.revitech.repository.UsersRepository;

@Service
@Transactional // 【追加】サービス層にトランザクションを付与
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Users> findAll() {
        return usersRepository.findAll();
    }

    public Optional<Users> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public Optional<Users> findById(Long id) {
        return usersRepository.findById(id);
    }

    public boolean isEmailTaken(String email) {
        return usersRepository.findByEmail(email).isPresent();
    }

    public Users save(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }
    
    /**
     * 名前またはメールアドレスでユーザーを検索する
     */
    public List<Users> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of(); // キーワードがない場合は空のリストを返す
        }
        // UsersRepositoryに定義されている効率的な検索メソッドを利用
        return usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }
 // 【新規追加】IDリストで複数のユーザーを取得するメソッド
    public List<Users> findAllById(List<Long> ids) {
        return usersRepository.findAllById(ids); // JpaRepository標準のメソッドを利用
    }
}