package com.example.revitech.service; // パッケージ名は実際の場所に合わせてください

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // saveメソッドで必要

// DTO と Entity をインポート
import com.example.revitech.dto.UserSearchDto;
import com.example.revitech.entity.Users;
// Repository をインポート
import com.example.revitech.repository.UsersRepository;
// Specification を使う場合はインポート
// import org.springframework.data.jpa.domain.Specification;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    /**
     * メールアドレスでユーザーを検索します。
     * ログインや重複チェックに使用します。
     * @param email 検索するメールアドレス
     * @return Users オブジェクトを含む Optional (見つからない場合は空)
     */
    public Optional<Users> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    /**
     * ID (users_id) でユーザーを検索します。
     * @param userId ユーザーのID
     * @return Users オブジェクトを含む Optional (見つからない場合は空)
     */
    public Optional<Users> findById(Long userId) {
        return usersRepository.findById(userId);
    }

    /**
     * 名前でユーザーを検索します。
     * ログイン（メールアドレスと併用）で使用します。
     * @param name 検索する名前
     * @return Users オブジェクトを含む Optional (見つからない場合は空)。Repository に findByName が必要。
     */
    public Optional<Users> findByName(String name) {
        // UsersRepository インターフェースに findByName(String name) メソッドが定義されていることを確認してください
        return usersRepository.findByName(name);
    }

    /**
     * メールアドレスが既に他のユーザーに使用されているかチェックします。
     * サインアップ時のバリデーションで使用します。
     * @param email チェックするメールアドレス
     * @return メールアドレスが使用されていれば true、そうでなければ false
     */
    public boolean isEmailTaken(String email) {
        return usersRepository.findByEmail(email).isPresent();
    }

    /**
     * 新規ユーザーを保存するか、既存ユーザーを更新します。
     * データベーストランザクションを管理します。
     * 注意: パスワードのハッシュ化はこのメソッドを呼び出す前 (例: Controller) で行う想定です。
     * @param user 保存する Users エンティティ
     * @return 保存された Users エンティティ (IDが付与されたり、タイムスタンプが更新されたりする可能性あり)
     */
    @Transactional // データベースへの書き込み操作には @Transactional を推奨
    public Users save(Users user) {
        // 必要であれば、保存前に追加のロジックを記述できます
        // 例: if (user.getStatus() == null) user.setStatus("active");
        return usersRepository.save(user); // Repository の save メソッドを呼び出す
    }

    /**
     * 全てのユーザーのリストを取得します。管理者機能などで使用する可能性があります。
     * @return 全ての Users エンティティのリスト
     */
    public List<Users> findAllUsers() {
        return usersRepository.findAll();
    }

    /**
     * 名前またはメールアドレスでユーザーを検索します (大文字小文字区別なし、部分一致)。
     * パスワードなどの機密データを除外した UserSearchDto のリストを返します。
     * UsersRepository に対応するメソッド (例: ContainingIgnoreCase や Specification を使用) が必要です。
     * @param keyword 検索キーワード
     * @return キーワードに一致する UserSearchDto のリスト
     */
    public List<UserSearchDto> findUsersByNameOrEmail(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of(); // キーワードが空なら空リストを返す
        }
        // 例1: カスタムリポジトリメソッドを使用 (UsersRepository で定義が必要)
        // List<Users> users = usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);

        // --- または ---

        // 例2: Specification を使用 (UsersRepository が JpaSpecificationExecutor<Users> を継承する必要あり)
        /*
        String pattern = "%" + keyword.toLowerCase() + "%";
        Specification<Users> spec = (root, query, cb) ->
            cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("email")), pattern)
            );
        List<Users> users = usersRepository.findAll(spec);
        */

        // 仮実装: 検索メソッドが未実装の場合、全ユーザーを返す (後で実際の検索ロジックに置き換える)
        List<Users> users = usersRepository.findAll(); // ★ 要修正: 実際の検索メソッドを呼び出す

        // 見つかった Users エンティティを UserSearchDto に変換
        return users.stream()
                     // 仮実装のための簡易フィルタ (実際の検索はRepositoryで行うべき)
                    .filter(u -> u.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                                 u.getEmail().toLowerCase().contains(keyword.toLowerCase()))
                    .map(user -> new UserSearchDto(user.getId(), user.getName(), user.getEmail()))
                    .collect(Collectors.toList());
    }

    // 他に必要なユーザー関連のビジネスロジックメソッドがあればここに追加...
    // 例: updateUserProfile, changePassword, deactivateUser など

}