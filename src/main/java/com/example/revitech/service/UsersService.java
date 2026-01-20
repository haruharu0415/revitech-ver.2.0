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
    private final EnrollmentRepository enrollmentRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherHashtagRepository teacherHashtagRepository;
    private static final String DEFAULT_ICON_PATH = "/images/haru.png";


    public UsersService(UsersRepository usersRepository, 
                        PasswordEncoder passwordEncoder,
                        TeacherReviewRepository teacherReviewRepository,
                        StudentProfileRepository studentProfileRepository,
                        TeacherProfileRepository teacherProfileRepository,
                        EnrollmentRepository enrollmentRepository,
                        SubjectRepository subjectRepository,
                        TeacherHashtagRepository teacherHashtagRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.teacherReviewRepository = teacherReviewRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.teacherHashtagRepository = teacherHashtagRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.subjectRepository = subjectRepository;
    }

    /**
     * 新規登録処理（ユーザー保存 ＋ 学科紐付け）
     * 先生(2)のみ承認待ち(pending)として登録します。
     */
    @Transactional
    public void register(SignupForm form) {
        Users user = new Users();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword())); 
        user.setRole(form.getRole());
        
        // ★修正: 先生(2)のみ pending(承認待ち)。生徒(1)と管理者(3)は active(即時有効)
        if (form.getRole() == 2) {
            user.setStatus("pending");
        } else {
            user.setStatus("active");
        }
        
        Users savedUser = usersRepository.save(user);

        if (savedUser.getRole() == 1 && form.getSubjectId() != null) {
            Enrollment enrollment = new Enrollment();
            enrollment.setUsersId(savedUser.getUsersId());
            enrollment.setSubjectId(form.getSubjectId());
            enrollmentRepository.save(enrollment);
        }
    }

    // --- ★ 管理者承認機能用に追加 ★ ---

    /**
     * 承認待ちのユーザー一覧を取得
     */
    public List<Users> findPendingUsers() {
        // 全ユーザーからステータスが "pending" のものを抽出
        return usersRepository.findAll().stream()
                .filter(u -> "pending".equals(u.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * ユーザーを承認（activeにする）
     */
    @Transactional
    public void approveUser(Integer userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus("active");
        usersRepository.save(user);
    }
    
    // --- ★ 管理画面検索用に追加 ★ ---
    public List<Users> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) { 
            return List.of(); 
        }
        // 名前またはメールにキーワードが含まれるユーザーを検索
        return usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }

    // --- 以下、既存のメソッド群 (変更なし) ---
    public List<TeacherListDto> getTeacherListDetails(String keyword) {
        List<Users> teachers;
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            if (kw.startsWith("#")) {
                List<TeacherHashtag> tags = teacherHashtagRepository.findByHashtagContaining(kw);
                List<Integer> teacherIds = tags.stream().map(TeacherHashtag::getTeacherId).distinct().collect(Collectors.toList());
                teachers = usersRepository.findAllById(teacherIds);
                teachers = teachers.stream().filter(u -> u.getRole() == 2).collect(Collectors.toList());
            } else {
                teachers = usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(kw, kw);
                teachers = teachers.stream().filter(u -> u.getRole() == 2).collect(Collectors.toList());
            }
        } else {
            teachers = findByRole(2);
        }
        return teachers.stream().map(teacher -> {
            List<String> subjects = List.of("担当科目");
            Double avgScore = 0.0;
            String iconBase64 = teacherProfileRepository.findByTeacherId(teacher.getUsersId())
                    .map(TeacherProfile::getIconBase64).orElse(null);
            List<String> hashtags = teacherHashtagRepository.findByTeacherId(teacher.getUsersId())
                    .stream().map(TeacherHashtag::getHashtag).collect(Collectors.toList());
            return new TeacherListDto(
                teacher.getUsersId(), teacher.getName(), teacher.getEmail(), subjects, avgScore, iconBase64, hashtags
            );
        }).collect(Collectors.toList());
    }

    public void softDeleteUser(Integer userId) {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus("deleted");
        usersRepository.save(user);
    }
    
    @Transactional
    public void updateProfile(Integer userId, String newName, String introduction, MultipartFile iconFile) throws IOException {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(newName);
        usersRepository.save(user);
        Integer role = user.getRole();
        if (role == null) return;
        if (role == 2 || role == 3) {
            TeacherProfile profile = teacherProfileRepository.findByTeacherId(userId).orElse(new TeacherProfile());
            if (profile.getTeacherId() == null) profile.setTeacherId(userId);
            if (introduction != null) profile.setIntroduction(introduction);
            if (iconFile != null && !iconFile.isEmpty()) profile.setIconData(iconFile.getBytes());
            teacherProfileRepository.save(profile);
        } else if (role == 1) {
            if (iconFile != null && !iconFile.isEmpty()) {
                String originalFilename = iconFile.getOriginalFilename();
                String ext = "";
                if (originalFilename != null && originalFilename.contains(".")) ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                String fileName = UUID.randomUUID().toString() + ext;
                String homeDir = System.getProperty("user.home");
                Path uploadDir = Paths.get(homeDir, "revitech_uploads", "images", "icons");
                if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
                Path filePath = uploadDir.resolve(fileName);
                Files.copy(iconFile.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                String iconPath = "/images/icons/" + fileName; 
                StudentProfile profile = studentProfileRepository.findById(userId).orElse(new StudentProfile());
                profile.setUsersId(userId);
                profile.setIconPicture(iconPath);
                studentProfileRepository.save(profile);
            }
        }
    }
    public String getUserIconPath(Integer userId) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user == null) return DEFAULT_ICON_PATH;
        String iconPath = null;
        Integer role = user.getRole();
        if (role != null) {
            if (role == 1) iconPath = studentProfileRepository.findById(userId).map(StudentProfile::getIconPicture).orElse(null);
            else if (role == 2 || role == 3) iconPath = teacherProfileRepository.findByTeacherId(userId).map(TeacherProfile::getIconBase64).orElse(null);
        }
        return (iconPath != null) ? iconPath : DEFAULT_ICON_PATH;
    }
    public String getTeacherIntroduction(Integer userId) {
        return teacherProfileRepository.findByTeacherId(userId).map(TeacherProfile::getIntroduction).orElse("");
    }
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

    public Users saveRawUser(Users user) { return usersRepository.save(user); }

}