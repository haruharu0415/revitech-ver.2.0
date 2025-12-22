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
     * ★修正: 自己紹介(introduction)を追加引数として受け取る
     */
    @Transactional
    public void updateProfile(Integer userId, String newName, String introduction, MultipartFile iconFile) throws IOException {
        
        // 1. 名前更新
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(newName);
        usersRepository.save(user);

        Integer role = user.getRole();
        if (role == null) return;

        // 2. 先生・管理者の更新処理 (DB保存)
        if (role == 2 || role == 3) {
            TeacherProfile profile = teacherProfileRepository.findByTeacherId(userId)
                .orElse(new TeacherProfile());
            
            if (profile.getTeacherId() == null) {
                profile.setTeacherId(userId);
            }

            // 自己紹介の更新
            if (introduction != null) {
                profile.setIntroduction(introduction);
            }

            // アイコン画像の更新 (DBへバイナリ保存)
            if (iconFile != null && !iconFile.isEmpty()) {
                profile.setIconData(iconFile.getBytes());
            }
            
            teacherProfileRepository.save(profile);
        }
        // 3. 生徒の更新処理 (ファイル保存)
        else if (role == 1) {
            // アイコンファイルがある場合のみ処理
            if (iconFile != null && !iconFile.isEmpty()) {
                String originalFilename = iconFile.getOriginalFilename();
                String ext = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String fileName = UUID.randomUUID().toString() + ext;
                
                String homeDir = System.getProperty("user.home");
                Path uploadDir = Paths.get(homeDir, "revitech_uploads", "images", "icons");
                
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path filePath = uploadDir.resolve(fileName);
                Files.copy(iconFile.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                String iconPath = "/images/icons/" + fileName; 

                StudentProfile profile = studentProfileRepository.findById(userId)
                    .orElse(new StudentProfile());
                profile.setUsersId(userId);
                profile.setIconPicture(iconPath);
                studentProfileRepository.save(profile);
            }
            // 生徒には自己紹介欄がないため何もしない (必要ならStudentProfileに追加)
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

        if (role != null) {
            if (role == 1) {
                // 生徒: ファイルパスを返す
                iconPath = studentProfileRepository.findById(userId)
                       .map(StudentProfile::getIconPicture).orElse(null);
            } else if (role == 2 || role == 3) {
                // ★修正: 先生: Base64データを返す
                iconPath = teacherProfileRepository.findByTeacherId(userId)
                       .map(TeacherProfile::getIconBase64).orElse(null);
            }
        }
        
        return (iconPath != null) ? iconPath : DEFAULT_ICON_PATH;
    }
    
    // ★追加: 先生の自己紹介文を取得するメソッド
    public String getTeacherIntroduction(Integer userId) {
        return teacherProfileRepository.findByTeacherId(userId)
                .map(TeacherProfile::getIntroduction)
                .orElse("");
    }

    // --- 以下、既存メソッド ---

    public List<TeacherListDto> getTeacherListDetails() {
        List<Users> teachers = findByRole(2); 
        return teachers.stream().map(teacher -> {
            List<String> subjects;
            // 科目ロジックは省略（既存のまま）
            switch (teacher.getName()) {
                case "福井先生": subjects = List.of("ハードウェア", "卒業制作"); break;
                case "佐藤先生": subjects = List.of("Java"); break;
                case "柴田先生": subjects = List.of("JavaScript", "PHP"); break;
                case "河野先生": subjects = List.of("アルゴリズム"); break;
                case "小宮山先生": subjects = List.of("ソフトウェア"); break;
                default: subjects = List.of("担当科目"); break;
            }
            Double avgScore = 0.0; 
            
            // ★Base64アイコン取得
            String iconBase64 = teacherProfileRepository.findByTeacherId(teacher.getUsersId())
                    .map(TeacherProfile::getIconBase64)
                    .orElse(null);

            return new TeacherListDto(
                teacher.getUsersId(),
                teacher.getName(),
                teacher.getEmail(),
                subjects,
                avgScore,
                iconBase64 // ★Base64を渡す
            );
        }).collect(Collectors.toList());
    }
    
    // (findAllStudentsGroupedBySubject, findAll, findByEmail等は変更なし)
    @Transactional(readOnly = true)
    public Map<String, List<Users>> findAllStudentsGroupedBySubject() {
        List<Users> students = findByRole(1);
        Map<String, List<Users>> groupedMap = new LinkedHashMap<>(); 
        groupedMap.put("情報処理科", new ArrayList<>());
        groupedMap.put("高度情報処理科", new ArrayList<>());
        for (Users student : students) {
            String subjectName = (student.getUsersId() % 2 == 0) ? "情報処理科" : "高度情報処理科";
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
    public Users saveRawUser(Users user) { return usersRepository.save(user); }
    public List<Users> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) { return List.of(); }
        return usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }
}