package com.example.revitech.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.revitech.dto.TeacherListDto;
import com.example.revitech.entity.StudentProfile;
import com.example.revitech.entity.TeacherProfile;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.StudentProfileRepository;
import com.example.revitech.repository.TeacherProfileRepository;
import com.example.revitech.repository.TeacherReviewRepository;
import com.example.revitech.repository.UsersRepository;

@Service
@Transactional
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeacherReviewRepository teacherReviewRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final TeacherProfileRepository teacherProfileRepository;

    // デフォルトアイコンパス
    private static final String DEFAULT_ICON_PATH = "/images/haru.png";

    public UsersService(UsersRepository usersRepository, 
                        PasswordEncoder passwordEncoder, 
                        TeacherReviewRepository teacherReviewRepository,
                        StudentProfileRepository studentProfileRepository,
                        TeacherProfileRepository teacherProfileRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.teacherReviewRepository = teacherReviewRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.teacherProfileRepository = teacherProfileRepository;
    }

    /**
     * プロフィール更新処理
     * 画像をアップロードし、student_profiles (または teacher_profiles) にパスを保存します。
     */
    @Transactional
    public void updateProfile(Integer userId, String newName, MultipartFile iconFile) throws IOException {
        
        // 1. Usersテーブルの名前を更新
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(newName);
        usersRepository.save(user);

        // 2. 画像ファイルが選択されている場合のみ保存処理を実行
        if (iconFile != null && !iconFile.isEmpty()) {
            
            // ファイル名を一意にする (UUID + 拡張子)
            String originalFilename = iconFile.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + ext;
            
            // ★ 保存先: ユーザーホーム/revitech_uploads/images/icons
            String homeDir = System.getProperty("user.home");
            Path uploadDir = Paths.get(homeDir, "revitech_uploads", "images", "icons");
            
            // フォルダがなければ作成
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // ファイルをディスクに保存
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(iconFile.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // DBに保存するパス（Webからアクセスするパス）
            String iconPath = "/images/icons/" + fileName; 
            
            // 3. 役割に応じて適切なプロフィールテーブルに保存
            Integer role = user.getRole();
            
            if (role != null && role == 1) { 
                // ★★★ 生徒の場合: student_profiles テーブルを更新 ★★★
                StudentProfile profile = studentProfileRepository.findById(userId)
                    .orElse(new StudentProfile()); // 存在しなければ新規作成
                
                profile.setUsersId(userId);      // IDをセット（必須）
                profile.setIconPicture(iconPath); // 画像パスをセット
                
                studentProfileRepository.save(profile);
                
            } else if (role != null && role == 2) { 
                // 先生の場合: teacher_profiles テーブルを更新
                TeacherProfile profile = teacherProfileRepository.findById(userId)
                    .orElse(new TeacherProfile());
                
                profile.setUsersId(userId);
                profile.setIconPicture(iconPath);
                
                teacherProfileRepository.save(profile);
            }
            // Role 3 (管理者) など他のロールの場合の処理が必要ならここに追加
        }
    }
    
    /**
     * アイコンパス取得
     */
    public String getUserIconPath(Integer userId) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user == null) return DEFAULT_ICON_PATH;

        String iconPath = null;
        Integer role = user.getRole();

        // ロールに応じて適切なテーブルから画像パスを取得
        if (role != null && role == 1) { // 生徒
             iconPath = studentProfileRepository.findById(userId)
                    .map(StudentProfile::getIconPicture).orElse(null);
        } else if (role != null && role == 2) { // 先生
             iconPath = teacherProfileRepository.findById(userId)
                    .map(TeacherProfile::getIconPicture).orElse(null);
        }
        
        // パスがなければデフォルト画像を返す
        return (iconPath != null) ? iconPath : DEFAULT_ICON_PATH;
    }

    // --- 以下、既存メソッド（省略なし） ---

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
    
    @Transactional(readOnly = true)
    public Map<String, List<Users>> findAllStudentsGroupedBySubject() {
        List<Users> students = findByRole(1);
        Map<String, List<Users>> groupedMap = new LinkedHashMap<>(); 
        
        groupedMap.put("情報処理科", new ArrayList<>());
        groupedMap.put("高度情報処理科", new ArrayList<>());
        
        for (Users student : students) {
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