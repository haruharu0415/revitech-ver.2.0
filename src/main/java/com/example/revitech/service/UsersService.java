package com.example.revitech.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.dto.UserSearchDto;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.UsersRepository; 

@Service
@Transactional // ★ DB書き込み(save)と読み取り(find)両方あるのでクラスに付与
public class UsersService {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    /**
     * メールアドレスでユーザーを検索します。
     */
    public Optional<Users> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    /**
     * ID (users_id) でユーザーを検索します。
     */
    public Optional<Users> findById(Long userId) {
        return usersRepository.findById(userId);
    }

    /**
     * 名前でユーザーを検索します。
     */
    public Optional<Users> findByName(String name) {
        return usersRepository.findByName(name);
    }

    /**
     * メールアドレスが既に他のユーザーに使用されているかチェックします。
     */
    public boolean isEmailTaken(String email) {
        // ★ .isPresent() より高速な existsByEmail を使う
        return usersRepository.existsByEmail(email);
    }

    // ★★★ これが LoginController から呼ばれるメソッド ★★★
    /**
     * ユーザー名 (name) が既に他のユーザーに使用されているかチェックします。
     */
    public boolean isNameTaken(String name) {
        // findByName メソッドを再利用
        return usersRepository.findByName(name).isPresent();
    }
    // ★★★ ここまで ★★★

    /**
     * 新規ユーザーを保存するか、既存ユーザーを更新します。
     */
    public Users save(Users user) {
        return usersRepository.save(user);
    }

    /**
     * 全てのユーザーのリストを取得します。
     */
    public List<Users> findAllUsers() {
        return usersRepository.findAll();
    }

    /**
     * 名前またはメールアドレスでユーザーを検索します (DTO)。
     */
    public List<UserSearchDto> findUsersByNameOrEmail(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        
        // ★ UsersRepository の findByNameContaining... メソッドを呼び出す
        // (DB側で検索するので高速)
        List<Users> users = usersRepository.findByNameContainingOrEmailContaining(keyword, keyword);

        // 見つかった Users エンティティを UserSearchDto に変換
        return users.stream()
                .map(user -> new UserSearchDto(user.getId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }
}