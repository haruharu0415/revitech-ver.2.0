package com.example.revitech.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.revitech.dto.TeacherListDto;
import com.example.revitech.dto.UserSearchDto;
import com.example.revitech.entity.BanWord;
import com.example.revitech.entity.Enrollment;
import com.example.revitech.entity.StudentProfile;
import com.example.revitech.entity.Subject;
import com.example.revitech.entity.TeacherHashtag;
import com.example.revitech.entity.TeacherImage;
import com.example.revitech.entity.TeacherProfile;
import com.example.revitech.entity.TeacherReview;
import com.example.revitech.entity.Users;
import com.example.revitech.form.SignupForm;
import com.example.revitech.repository.BanWordRepository;
import com.example.revitech.repository.EnrollmentRepository;
import com.example.revitech.repository.StudentProfileRepository;
import com.example.revitech.repository.SubjectRepository;
import com.example.revitech.repository.TeacherHashtagRepository;
import com.example.revitech.repository.TeacherImageRepository;
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
    private final TeacherImageRepository teacherImageRepository;
    private final BanWordRepository banWordRepository;
    
    private static final String DEFAULT_ICON_PATH = "/images/haru.png";

    public UsersService(UsersRepository usersRepository, 
                        PasswordEncoder passwordEncoder,
                        TeacherReviewRepository teacherReviewRepository,
                        StudentProfileRepository studentProfileRepository,
                        TeacherProfileRepository teacherProfileRepository,
                        EnrollmentRepository enrollmentRepository,
                        SubjectRepository subjectRepository,
                        TeacherHashtagRepository teacherHashtagRepository,
                        TeacherImageRepository teacherImageRepository,
                        BanWordRepository banWordRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.teacherReviewRepository = teacherReviewRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.teacherHashtagRepository = teacherHashtagRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.subjectRepository = subjectRepository;
        this.teacherImageRepository = teacherImageRepository;
        this.banWordRepository = banWordRepository;
    }

    // --- 検索機能 ---
    public List<TeacherListDto> getTeacherListDetails(String keyword) {
        List<Users> teachers;
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            Set<Integer> teacherIds = new HashSet<>();
            usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(kw, kw)
                           .forEach(u -> teacherIds.add(u.getUsersId()));
            teacherHashtagRepository.findByHashtagContaining(kw)
                                    .forEach(t -> teacherIds.add(t.getTeacherId()));
            if (!teacherIds.isEmpty()) {
                teachers = usersRepository.findAllById(teacherIds);
                teachers = teachers.stream().filter(u -> u.getRole() == 2).collect(Collectors.toList());
            } else {
                teachers = new ArrayList<>(); 
            }
        } else {
            teachers = findByRole(2);
        }

        List<Subject> allSubjects = subjectRepository.findAll();
        Set<String> subjectHashtagSet = allSubjects.stream()
                .map(s -> "#" + s.getSubjectName())
                .collect(Collectors.toSet());

        return teachers.stream()
            .filter(teacher -> !"deleted".equals(teacher.getStatus()))
            .map(teacher -> {
                String iconBase64 = null;
                TeacherProfile tp = teacherProfileRepository.findByTeacherId(teacher.getUsersId()).orElse(null);
                if (tp != null && tp.getIconData() != null) {
                     iconBase64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(tp.getIconData());
                }

                List<String> rawHashtags = teacherHashtagRepository.findByTeacherId(teacher.getUsersId())
                        .stream().map(TeacherHashtag::getHashtag).collect(Collectors.toList());

                List<String> subjectTags = new ArrayList<>();
                List<String> otherTags = new ArrayList<>();

                for (String tag : rawHashtags) {
                    if (subjectHashtagSet.contains(tag)) {
                        subjectTags.add(tag.startsWith("#") ? tag.substring(1) : tag);
                    } else {
                        otherTags.add(tag);
                    }
                }

                return new TeacherListDto(teacher.getUsersId(), teacher.getName(), teacher.getEmail(), subjectTags, 0.0, iconBase64, otherTags);
            }).collect(Collectors.toList());
    }

    // --- その他メソッド ---
    @Transactional
    public void addBanWord(Integer teacherId, String word) {
        if (word == null || word.trim().isEmpty()) return;
        String cleanWord = word.trim();
        if (!banWordRepository.existsByTeacherIdAndWord(teacherId, cleanWord)) {
            BanWord bw = new BanWord();
            bw.setTeacherId(teacherId);
            bw.setWord(cleanWord);
            banWordRepository.save(bw);
        }
    }
    public List<BanWord> getBanWordsByTeacherId(Integer teacherId) { return banWordRepository.findByTeacherId(teacherId); }
    @Transactional
    public void deleteBanWord(Integer banId) { banWordRepository.deleteById(banId); }
    public Optional<BanWord> findBanWordById(Integer id) { return banWordRepository.findById(id); }

    // --- アカウント登録 ---
    @Transactional
    public void register(SignupForm form) {
        Users user = new Users();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword())); 
        user.setRole(form.getRole());
        user.setStatus(form.getRole() == 2 ? "pending" : "active");
        
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now); user.setCreateAt(now); user.setUpdatedAt(now); user.setUpdateAt(now);
        Users savedUser = usersRepository.save(user);

        if (savedUser.getRole() == 1 && form.getSubjectId() != null) {
            Enrollment enrollment = new Enrollment();
            enrollment.setUsersId(savedUser.getUsersId());
            enrollment.setSubjectId(form.getSubjectId());
            enrollmentRepository.save(enrollment);
        }
        else if (savedUser.getRole() == 2 && form.getTeacherSubjectIds() != null) {
            for (Integer subId : form.getTeacherSubjectIds()) {
                subjectRepository.findById(subId).ifPresent(subject -> {
                    TeacherHashtag tag = new TeacherHashtag();
                    tag.setTeacherId(savedUser.getUsersId());
                    tag.setHashtag("#" + subject.getSubjectName()); 
                    teacherHashtagRepository.save(tag);
                });
            }
        }
    }
    
    public List<Users> findPendingUsers() { return usersRepository.findAll().stream().filter(u -> "pending".equals(u.getStatus())).collect(Collectors.toList()); }
    @Transactional
    public void approveUser(Integer userId) { Users user = usersRepository.findById(userId).orElseThrow(); user.setStatus("active"); usersRepository.save(user); }
    
    public List<UserSearchDto> searchUsers(String keyword, Integer excludeUserId) {
        if (keyword == null || keyword.trim().isEmpty()) return List.of(); 
        List<Users> users = usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
        return users.stream()
                .filter(user -> !user.getUsersId().equals(excludeUserId))
                .map(user -> {
                    String iconUrl = getUserIconPath(user.getUsersId());
                    return new UserSearchDto(
                        user.getUsersId(),
                        user.getName(),
                        user.getEmail(),
                        iconUrl,
                        user.getRole()
                    );
                }).collect(Collectors.toList());
    }

    @Transactional
    public void updateProfile(Integer userId, String newName, String introduction, MultipartFile iconFile, List<Integer> teacherSubjectIds) throws IOException {
        Users user = usersRepository.findById(userId).orElseThrow();
        user.setName(newName);
        usersRepository.save(user);

        if (user.getRole() == 2 || user.getRole() == 3) {
            TeacherProfile profile = teacherProfileRepository.findByTeacherId(userId).orElse(new TeacherProfile());
            profile.setTeacherId(userId);
            if (introduction != null) profile.setIntroduction(introduction);
            if (iconFile != null && !iconFile.isEmpty()) {
                profile.setIconData(iconFile.getBytes());
            }
            teacherProfileRepository.save(profile);
            
            if (teacherSubjectIds != null) {
                List<Subject> allSubjects = subjectRepository.findAll();
                Set<String> allSubjectNames = allSubjects.stream()
                        .map(s -> "#" + s.getSubjectName())
                        .collect(Collectors.toSet());

                List<TeacherHashtag> currentTags = teacherHashtagRepository.findByTeacherId(userId);

                Set<String> newSubjectTags = new HashSet<>();
                for (Integer subId : teacherSubjectIds) {
                    subjectRepository.findById(subId).ifPresent(s -> newSubjectTags.add("#" + s.getSubjectName()));
                }

                for (TeacherHashtag tag : currentTags) {
                    if (allSubjectNames.contains(tag.getHashtag()) && !newSubjectTags.contains(tag.getHashtag())) {
                        teacherHashtagRepository.delete(tag);
                    }
                }

                Set<String> currentTagNames = currentTags.stream().map(TeacherHashtag::getHashtag).collect(Collectors.toSet());
                for (String newTagStr : newSubjectTags) {
                    if (!currentTagNames.contains(newTagStr)) {
                        TeacherHashtag newTag = new TeacherHashtag();
                        newTag.setTeacherId(userId);
                        newTag.setHashtag(newTagStr);
                        teacherHashtagRepository.save(newTag);
                    }
                }
            }
        } 
        else if (user.getRole() == 1) {
            if (iconFile != null && !iconFile.isEmpty()) {
                String fileName = "student_" + userId + "_" + System.currentTimeMillis() + ".png";
                Path srcPath = Paths.get("src/main/resources/static/images/icons/");
                if (!Files.exists(srcPath)) Files.createDirectories(srcPath);
                try (var inputStream = iconFile.getInputStream()) {
                    Files.copy(inputStream, srcPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                }
                Path targetPath = Paths.get("target/classes/static/images/icons/");
                if (!Files.exists(targetPath)) Files.createDirectories(targetPath);
                try (var inputStream = iconFile.getInputStream()) {
                    Files.copy(inputStream, targetPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                }
                StudentProfile sp = studentProfileRepository.findById(userId).orElse(new StudentProfile());
                sp.setUsersId(userId); 
                sp.setIconPicture("/images/icons/" + fileName);
                studentProfileRepository.save(sp);
            }
        }
    }

    public List<TeacherReview> getDisclosureList(Users user) {
        List<TeacherReview> allReviews = teacherReviewRepository.findAll();
        
        return allReviews.stream()
            .filter(r -> r.getDisclosureStatus() != null && (r.getDisclosureStatus() == 1 || r.getDisclosureStatus() == 2))
            .filter(r -> {
                if (user.getRole() == 3) return true;
                if (user.getRole() == 2) return r.getTeacherId().equals(user.getUsersId());
                return false;
            })
            .sorted(Comparator.comparing(TeacherReview::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    // ★★★ 追加: 先生への開示許可済みレビューを全て取得するメソッド ★★★
    public List<TeacherReview> getGrantedReviews(Integer teacherId) {
        List<TeacherReview> allReviews = teacherReviewRepository.findAll();
        
        return allReviews.stream()
            .filter(r -> r.getTeacherId().equals(teacherId)) // 自分宛て
            .filter(r -> Boolean.TRUE.equals(r.getIsDisclosureGranted())) // 許可されたもの
            .sorted(Comparator.comparing(TeacherReview::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    public String getTeacherSubjectNames(Integer teacherId) {
        List<TeacherHashtag> tags = teacherHashtagRepository.findByTeacherId(teacherId);
        List<Subject> subjects = subjectRepository.findAll();
        List<String> names = new ArrayList<>();
        
        for (TeacherHashtag tag : tags) {
            String tagName = tag.getHashtag().startsWith("#") ? tag.getHashtag().substring(1) : tag.getHashtag();
            for (Subject sub : subjects) {
                if (sub.getSubjectName().equals(tagName)) {
                    names.add(sub.getSubjectName());
                    break;
                }
            }
        }
        if (names.isEmpty()) {
            return "担当なし";
        }
        return String.join(", ", names);
    }
    
    public List<Integer> getTeacherSubjectIds(Integer teacherId) {
        List<TeacherHashtag> tags = teacherHashtagRepository.findByTeacherId(teacherId);
        List<Subject> subjects = subjectRepository.findAll();
        List<Integer> ids = new ArrayList<>();
        
        for (TeacherHashtag tag : tags) {
            String tagName = tag.getHashtag().startsWith("#") ? tag.getHashtag().substring(1) : tag.getHashtag();
            for (Subject sub : subjects) {
                if (sub.getSubjectName().equals(tagName)) {
                    ids.add(sub.getSubjectId());
                    break;
                }
            }
        }
        return ids;
    }

    public List<TeacherHashtag> getTeacherHashtags(Integer teacherId) { return teacherHashtagRepository.findByTeacherId(teacherId); }
    @Transactional
    public void addHashtag(Integer teacherId, String hashtag) {
        if (hashtag == null || hashtag.trim().isEmpty()) return;
        String tag = hashtag.trim().startsWith("#") ? hashtag.trim() : "#" + hashtag.trim();
        TeacherHashtag newTag = new TeacherHashtag();
        newTag.setTeacherId(teacherId);
        newTag.setHashtag(tag);
        teacherHashtagRepository.save(newTag);
    }
    @Transactional
    public void deleteHashtag(Integer hashtagId) { teacherHashtagRepository.deleteById(hashtagId); }
    public Optional<TeacherHashtag> findHashtagById(Integer id) { return teacherHashtagRepository.findById(id); }
    public List<TeacherImage> getTeacherImages(Integer teacherId) { return teacherImageRepository.findByTeacherId(teacherId); }
    public TeacherProfile findTeacherProfile(Integer teacherId) { return teacherProfileRepository.findByTeacherId(teacherId).orElse(null); }
    
    public String getUserIconPath(Integer userId) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user == null) return DEFAULT_ICON_PATH;
        if (user.getRole() == 1) return studentProfileRepository.findById(userId).map(StudentProfile::getIconPicture).orElse(DEFAULT_ICON_PATH);
        TeacherProfile tp = teacherProfileRepository.findByTeacherId(userId).orElse(null);
        if (tp != null && tp.getIconData() != null) return "data:image/png;base64," + Base64.getEncoder().encodeToString(tp.getIconData());
        return DEFAULT_ICON_PATH;
    }
    
    public String getTeacherIntroduction(Integer userId) { return teacherProfileRepository.findByTeacherId(userId).map(TeacherProfile::getIntroduction).orElse(""); }
    public String getStudentSubjectName(Integer userId) { return enrollmentRepository.findByUsersId(userId).flatMap(e -> subjectRepository.findById(e.getSubjectId())).map(Subject::getSubjectName).orElse("所属なし"); }
    public boolean isEmailTaken(String email) { return usersRepository.findByEmail(email).isPresent(); }
    public Optional<Users> findByEmail(String email) { return usersRepository.findByEmail(email); }
    public Optional<Users> findByNameOrEmail(String identifier) { return usersRepository.findByNameOrEmail(identifier, identifier); }
    public Optional<Users> findById(Integer id) { return usersRepository.findById(id); }
    public List<Users> findByRole(Integer role) { return usersRepository.findByRole(role); }
    public Optional<Users> findByName(String name) { return usersRepository.findByName(name); }
    public Users saveRawUser(Users user) { return usersRepository.save(user); }
    public void softDeleteUser(Integer userId) { Users user = usersRepository.findById(userId).orElseThrow(); user.setStatus("deleted"); usersRepository.save(user); }
    public Map<String, List<Users>> findAllStudentsGroupedBySubject() {
        Map<String, List<Users>> groupedMap = new LinkedHashMap<>();
        for (Subject subject : subjectRepository.findAll()) {
            List<Users> students = new ArrayList<>();
            for (Enrollment en : enrollmentRepository.findBySubjectId(subject.getSubjectId())) {
                usersRepository.findById(en.getUsersId()).ifPresent(students::add);
            }
            groupedMap.put(subject.getSubjectName(), students);
        }
        return groupedMap;
    }
}