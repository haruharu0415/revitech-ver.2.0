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
import com.example.revitech.entity.Enrollment;
import com.example.revitech.entity.StudentProfile;
import com.example.revitech.entity.Subject;
import com.example.revitech.entity.TeacherHashtag;
import com.example.revitech.entity.TeacherProfile;
import com.example.revitech.entity.Users;
import com.example.revitech.form.SignupForm;
import com.example.revitech.repository.EnrollmentRepository;
import com.example.revitech.repository.StudentProfileRepository;
import com.example.revitech.repository.SubjectRepository;
import com.example.revitech.repository.TeacherHashtagRepository;
import com.example.revitech.repository.TeacherProfileRepository;
import com.example.revitech.repository.UsersRepository;

@Service
@Transactional
public class UsersService {

    private final UsersRepository usersRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeacherProfileRepository teacherProfileRepository;
    private final TeacherHashtagRepository teacherHashtagRepository;
    private final StudentProfileRepository studentProfileRepository;

    // アイコン保存用ディレクトリ (環境に合わせて変更してください)
    private static final String ICON_UPLOAD_DIR = System.getProperty("user.home") + "/revitech_uploads/images/icons/";

    public UsersService(UsersRepository usersRepository, 
                        EnrollmentRepository enrollmentRepository,
                        SubjectRepository subjectRepository, 
                        PasswordEncoder passwordEncoder,
                        TeacherProfileRepository teacherProfileRepository,
                        TeacherHashtagRepository teacherHashtagRepository,
                        StudentProfileRepository studentProfileRepository) {
        this.usersRepository = usersRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.subjectRepository = subjectRepository;
        this.passwordEncoder = passwordEncoder;
        this.teacherProfileRepository = teacherProfileRepository;
        this.teacherHashtagRepository = teacherHashtagRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    // 新規登録処理
    public void register(SignupForm form) {
        // Usersテーブルへの保存
        Users user = new Users();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole(form.getRole());
        user.setStatus("active");
        user.setChatSortOrder(1); // デフォルト日付順
        
        Users savedUser = usersRepository.save(user);

        // 生徒(Role=1)の場合、EnrollmentとStudentProfileを作成
        if (form.getRole() == 1) {
            // 学科登録
            if (form.getSubjectId() != null) {
                Enrollment enrollment = new Enrollment();
                enrollment.setUsersId(savedUser.getUsersId());
                enrollment.setSubjectId(form.getSubjectId());
                enrollmentRepository.save(enrollment);
            }
            // プロフィール初期化
            StudentProfile sp = new StudentProfile();
            sp.setUsersId(savedUser.getUsersId());
            sp.setIconPicture("default.png"); // デフォルト画像
            sp.setIntroduction("");
            studentProfileRepository.save(sp);
        }
        // 先生(Role=2)の場合、TeacherProfileを作成
        else if (form.getRole() == 2) {
            TeacherProfile tp = new TeacherProfile();
            tp.setTeacherId(savedUser.getUsersId());
            tp.setIntroduction("");
            // iconDataはnullのままにしておき、表示時にデフォルト処理をするか、ここで空データを入れる
            teacherProfileRepository.save(tp);
        }
        // 管理者(Role=3)の場合
        else if (form.getRole() == 3) {
            // 必要なら管理者用プロフィール作成
        }
    }
    
    // 生のUsersエンティティを保存 (CommandLineRunner用など)
    public void saveRawUser(Users user) {
        usersRepository.save(user);
    }

    // プロフィール更新 (名前、自己紹介、アイコン)
    public void updateProfile(Integer userId, String newName, String introduction, MultipartFile iconFile) throws IOException {
        // 1. Usersテーブル更新 (名前)
        Users user = usersRepository.findById(userId).orElseThrow();
        user.setName(newName);
        usersRepository.save(user);

        // 2. ロールごとのプロフィール更新 (自己紹介)
        if (user.getRole() == 2) { // 先生
            TeacherProfile profile = teacherProfileRepository.findByTeacherId(userId).orElse(new TeacherProfile());
            profile.setTeacherId(userId);
            profile.setIntroduction(introduction);
            
            // アイコン更新 (先生はDBのBLOB保存)
            if (iconFile != null && !iconFile.isEmpty()) {
                profile.setIconData(iconFile.getBytes());
            }
            teacherProfileRepository.save(profile);

        } else if (user.getRole() == 1) { // 生徒
            StudentProfile profile = studentProfileRepository.findById(userId).orElse(new StudentProfile());
            profile.setUsersId(userId);
            profile.setIntroduction(introduction);
            
            // アイコン更新 (生徒はファイルパス保存)
            if (iconFile != null && !iconFile.isEmpty()) {
                String fileName = saveIconFile(iconFile);
                profile.setIconPicture(fileName);
            }
            studentProfileRepository.save(profile);
        }
        // 管理者は名前変更のみ対応
    }

    // アイコン画像をファイルシステムに保存するメソッド
    private String saveIconFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(ICON_UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // UUIDでユニークなファイル名生成
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + ext;
        
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        
        return fileName;
    }

    // アイコン画像のパス/データを取得
    public String getUserIconPath(Integer userId) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user == null) return "/images/icons/default.png";

        if (user.getRole() == 2) { // 先生
            return teacherProfileRepository.findByTeacherId(userId)
                    .map(TeacherProfile::getIconBase64)
                    .orElse(null); // nullの場合はフロントでデフォルト表示
        } else if (user.getRole() == 1) { // 生徒
            return studentProfileRepository.findById(userId)
                    .map(p -> "/images/icons/" + p.getIconPicture())
                    .orElse("/images/icons/default.png");
        }
        return "/images/icons/default.png";
    }
    
    // 先生一覧用詳細データの取得
    public List<TeacherListDto> getTeacherListDetails(String keyword) {
        List<Users> teachers;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // UsersRepositoryにメソッド追加が必要：findByNameContainingAndRole(keyword, 2) など
            // ここでは簡易的に全件取得してからフィルタリング
            teachers = usersRepository.findByRole(2).stream()
                    .filter(u -> u.getName().contains(keyword))
                    .collect(Collectors.toList());
        } else {
            teachers = usersRepository.findByRole(2);
        }

        return teachers.stream().map(t -> {
            TeacherProfile prof = teacherProfileRepository.findByTeacherId(t.getUsersId()).orElse(new TeacherProfile());
            List<String> tags = teacherHashtagRepository.findByTeacherId(t.getUsersId())
                                .stream().map(TeacherHashtag::getHashtag).collect(Collectors.toList());
            
            // 本来は平均スコア計算が必要ですが、ここでは仮置き
            Double avg = 0.0; 
            
            return new TeacherListDto(
                t.getUsersId(),
                t.getName(),
                t.getEmail(),
                new ArrayList<>(), // 担当教科リスト(必要なら取得)
                avg,
                prof.getIconBase64(),
                tags
            );
        }).collect(Collectors.toList());
    }
    
    public void softDeleteUser(Integer userId) {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus("deleted");
        usersRepository.save(user);
    }
    
    // ユーザー検索（名前やメールで）
    public List<Users> searchUsers(String keyword) {
        return usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }
    
    // 先生の自己紹介取得
    public String getTeacherIntroduction(Integer userId) {
        return teacherProfileRepository.findByTeacherId(userId).map(TeacherProfile::getIntroduction).orElse("");
    }

    // 学科ごとの生徒リスト取得
    public Map<String, List<Users>> findAllStudentsGroupedBySubject() {
        List<Subject> allSubjects = subjectRepository.findAll();
        Map<String, List<Users>> groupedMap = new LinkedHashMap<>();
        
        for (Subject subject : allSubjects) {
            List<Enrollment> enrollments = enrollmentRepository.findBySubjectId(subject.getSubjectId());
            List<Users> studentsInSubject = new ArrayList<>();
            for (Enrollment en : enrollments) {
                usersRepository.findById(en.getUsersId()).ifPresent(studentsInSubject::add);
            }
            groupedMap.put(subject.getSubjectName(), studentsInSubject);
        }
        return groupedMap;
    }

    // ★★★ 追加: 生徒のユーザーIDから学科名を取得する ★★★
    public String getStudentSubjectName(Integer userId) {
        return enrollmentRepository.findByUsersId(userId)
                .flatMap(enrollment -> subjectRepository.findById(enrollment.getSubjectId()))
                .map(Subject::getSubjectName)
                .orElse("所属なし"); // 見つからない場合
    }

    // --- 以下、既存の便利メソッド群 ---

    public boolean isEmailTaken(String email) {
        return usersRepository.findByEmail(email).isPresent();
    }

    public Optional<Users> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public Optional<Users> findById(Integer id) {
        return usersRepository.findById(id);
    }
    
    public List<Users> findByRole(Integer role) {
        return usersRepository.findByRole(role);
    }
    
    public Optional<Users> findByName(String name) {
        return usersRepository.findByName(name);
    }
}