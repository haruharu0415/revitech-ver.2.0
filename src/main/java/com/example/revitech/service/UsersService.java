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
import com.example.revitech.entity.TeacherProfile;
import com.example.revitech.entity.Users;
import com.example.revitech.form.SignupForm;
import com.example.revitech.repository.EnrollmentRepository;
import com.example.revitech.repository.StudentProfileRepository;
import com.example.revitech.repository.SubjectRepository;
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

    public UsersService(UsersRepository usersRepository, 
                        PasswordEncoder passwordEncoder,
                        TeacherReviewRepository teacherReviewRepository,
                        StudentProfileRepository studentProfileRepository,
                        TeacherProfileRepository teacherProfileRepository,
                        EnrollmentRepository enrollmentRepository,
                        SubjectRepository subjectRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.teacherReviewRepository = teacherReviewRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.subjectRepository = subjectRepository;
    }

    /**
     * 新規登録処理（ユーザー保存 ＋ 学科紐付け）
     */
    @Transactional
    public void register(SignupForm form) {
        Users user = new Users();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword())); 
        user.setRole(form.getRole());
        user.setStatus("active");
        
        Users savedUser = usersRepository.save(user);

        if (savedUser.getRole() == 1 && form.getSubjectId() != null) {
            Enrollment enrollment = new Enrollment();
            enrollment.setUsersId(savedUser.getUsersId());
            enrollment.setSubjectId(form.getSubjectId());
            enrollmentRepository.save(enrollment);
        }
    }

    /**
     * ★★★ 今回追加: 教員の自己紹介文を取得 ★★★
     */
    public String getTeacherIntroduction(Integer teacherId) {
        return teacherProfileRepository.findByTeacherId(teacherId)
                .map(TeacherProfile::getIntroduction)
                .orElse("");
    }

    /**
     * 学科ごとに生徒をグループ化して取得
     */
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

    public String getUserIconPath(Integer userId) {
        return studentProfileRepository.findById(userId)
                .map(p -> "/images/icons/" + p.getIconPicture())
                .orElse("/images/haru.png");
    }

    public List<TeacherListDto> getTeacherListDetails() {
        List<Users> teachers = usersRepository.findByRole(2);
        return teachers.stream().map(teacher -> {
            List<String> subjects = List.of("担当科目未設定"); 
            Double avg = 0.0;
            TeacherProfile profile = teacherProfileRepository.findByTeacherId(teacher.getUsersId()).orElse(null);
            String icon = (profile != null) ? profile.getIconBase64() : null;
            return new TeacherListDto(teacher.getUsersId(), teacher.getName(), teacher.getEmail(), subjects, avg, icon);
        }).collect(Collectors.toList());
    }

    public void updateProfile(Integer userId, String name, String introduction, MultipartFile iconFile) throws IOException {
        Users user = usersRepository.findById(userId).orElseThrow();
        user.setName(name);
        usersRepository.save(user);

        if (user.getRole() == 2 || user.getRole() == 3) {
            TeacherProfile tp = teacherProfileRepository.findByTeacherId(userId).orElse(new TeacherProfile());
            tp.setTeacherId(userId);
            tp.setIntroduction(introduction);
            if (iconFile != null && !iconFile.isEmpty()) tp.setIconData(iconFile.getBytes());
            teacherProfileRepository.save(tp);
        } else {
            StudentProfile sp = studentProfileRepository.findById(userId).orElse(new StudentProfile());
            sp.setUsersId(userId);
            sp.setIntroduction(introduction);
            if (iconFile != null && !iconFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + iconFile.getOriginalFilename();
                Path path = Paths.get(System.getProperty("user.home"), "revitech_uploads", "images", "icons", fileName);
                Files.createDirectories(path.getParent());
                Files.copy(iconFile.getInputStream(), path);
                sp.setIconPicture(fileName);
            }
            studentProfileRepository.save(sp);
        }
    }

    public Users saveRawUser(Users user) {
        return usersRepository.save(user);
    }

    public List<Users> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return List.of();
        return usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }
}