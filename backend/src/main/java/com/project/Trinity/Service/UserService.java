package com.project.Trinity.Service;

import com.project.Trinity.Entity.PasswordResetToken;
import com.project.Trinity.Entity.Role;
import com.project.Trinity.Entity.User;
import com.project.Trinity.Entity.AuditLog;
import com.project.Trinity.Entity.Password;
import com.project.Trinity.Entity.Status;
import com.project.Trinity.Repository.PasswordResetTokenRepository;
import com.project.Trinity.Repository.RefreshTokenRepository;
import com.project.Trinity.Repository.UserRepository;
import com.project.Trinity.Repository.AuditLogRepository;
import com.project.Trinity.Repository.PasswordRepository;
import com.project.Trinity.DTO.UserResponse;
import com.project.Trinity.Service.PasswordService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordService passwordService;
    private final AuditLogRepository auditLogRepository;
    private final PasswordRepository passwordRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    
    public UserService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            PasswordService passwordService,
            AuditLogRepository auditLogRepository,PasswordRepository passwordRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.passwordService = passwordService;
        this.auditLogRepository = auditLogRepository;
        this.passwordRepository = passwordRepository;
        
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        // Hesap INACTIVE ve frozenAt 30 günden eski değilse, aktif hale getir
        if (user.getStatus() == Status.INACTIVE && user.getFrozenAt() != null) {
            long daysFrozen = ChronoUnit.DAYS.between(user.getFrozenAt(), LocalDateTime.now());
            if (daysFrozen < 30) {
                user.setStatus(Status.ACTIVE);
                user.setFrozenAt(null); // Dondurma zamanını sıfırla
                userRepository.save(user);

                // Denetim kaydı ekle
                AuditLog auditLog = new AuditLog();
                auditLog.setAction("Hesap aktif hale getirildi: " + username);
                auditLog.setTimestamp(LocalDateTime.now());
                auditLogRepository.save(auditLog);
            }
        }

        return user;
    }

    @Transactional
    public UserResponse createUser(String username, String password, String email, String phone) {
        // Sadece 6 parametreli fonksiyonu çağır, burada tekrar kayıt yapma!
        return createUser(username, password, email, phone, "ACTIVE", "USER");
    }

    @Transactional
    public UserResponse createUser(String username, String password, String email, String phone, String status, String role) {
        // Null kontrolleri ekleyelim
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Kullanıcı adı boş olamaz");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email boş olamaz");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefon boş olamaz");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Şifre boş olamaz");
        }
        if (status == null || status.trim().isEmpty()) {
            status = "ACTIVE"; // Varsayılan değer
        }
        if (role == null || role.trim().isEmpty()) {
            role = "USER"; // Varsayılan değer
        }

        // Mevcut kontroller
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException("Kullanıcı adı zaten mevcut: " + username);
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email zaten mevcut: " + email);
        }
        if (userRepository.findByPhone(phone).isPresent()) {
            throw new IllegalArgumentException("Telefon zaten mevcut: " + phone);
        }

        User newUser = new User();
        newUser.setUsername(username.trim());
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setEmail(email.trim());
        newUser.setPhone(phone.trim());
        newUser.setStatus(Status.valueOf(status));
        newUser.setRole(Role.valueOf(role));

        User savedUser = userRepository.save(newUser);

        AuditLog auditLog = new AuditLog();
        auditLog.setAction("Kullanıcı eklendi: " + username);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);

        // UserResponse'u status ve role ile birlikte döndür
        return new UserResponse(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getPhone(),
            savedUser.getStatus().toString(),
            savedUser.getRole().toString()
        );
    }

    public UserResponse updateUser(Long id, String newUsername, String password, String email, String phone, String status, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + id));
        user.setUsername(newUsername);
        if (password != null && !password.trim().isEmpty() && password.length() > 0) {
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setEmail(email);
        user.setPhone(phone);
        if (status != null) {
            user.setStatus(Status.valueOf(status));
        }
        if (role != null) {
            user.setRole(Role.valueOf(role));
        }
        User updatedUser = userRepository.save(user);

        AuditLog auditLog = new AuditLog();
        auditLog.setAction("Kullanıcı güncellendi: " + newUsername);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);

        return new UserResponse(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail(), updatedUser.getPhone());
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getPhone()));
    }

    public long countUsers() {
        return userRepository.count();
    }

    @Transactional
    public void deleteUser(Long id) {
        refreshTokenRepository.deleteByUserId(id);
        userRepository.deleteById(id);

        AuditLog auditLog = new AuditLog();
        auditLog.setAction("Kullanıcı silindi: ID " + id);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }

    @Transactional
    public void uploadProfilePicture(String username, byte[] imageData) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
        user.setProfilePicture(imageData);
        userRepository.save(user);

        AuditLog auditLog = new AuditLog();
        auditLog.setAction("Profil resmi güncellendi: " + username);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }

    @Transactional
    public void freezeAccount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
        user.setStatus(Status.INACTIVE); // FROZEN yerine INACTIVE kullanıyoruz
        user.setFrozenAt(LocalDateTime.now());
        userRepository.save(user);

        AuditLog auditLog = new AuditLog();
        auditLog.setAction("Hesap donduruldu: " + username);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }

    public void sendResetLink(String emailOrPhone) {
        User user = userRepository.findByEmail(emailOrPhone)
                .orElseGet(() -> userRepository.findByPhone(emailOrPhone)
                        .orElseThrow(() -> new IllegalArgumentException("No account found with this email or phone")));

        String resetCode = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);

        PasswordResetToken token = new PasswordResetToken(resetCode, user, expiryDate);
        tokenRepository.save(token);

        try {
            emailService.sendResetCodeEmail(user.getEmail(), resetCode);
        } catch (Exception e) {
            throw new RuntimeException("E-posta gönderimi başarısız: " + e.getMessage());
        }
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Geçersiz sıfırlama kodu"));

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Sıfırlama kodu süresi dolmuş");
        }

        User user = resetToken.getUser();
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Yeni şifre en az 8 karakter olmalı");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        AuditLog auditLog = new AuditLog();
        auditLog.setAction("Şifre sıfırlandı: " + user.getUsername());
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);

        tokenRepository.delete(resetToken);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getPhone());
    }

    public String getCurrentAdminUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public long getTotalPasswordCount() {
        return passwordService.countPasswords();
    }

    public long getTotalUserCount() {
        return userRepository.count();
    }

    public List<String> getRecentActions() {
        return auditLogRepository.findTop10ByOrderByTimestampDesc()
                .stream()
                .map(AuditLog::getAction)
                .collect(Collectors.toList());
    }
    
    public Map<String, Long> getPasswordViewTrend(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
       
		List<Password> passwords = passwordRepository.findByUser(user);

        Map<String, Long> trend = new TreeMap<>();
        LocalDateTime current = startDate;
        while (current.isBefore(LocalDateTime.now())) {
            String dateKey = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            trend.put(dateKey, 0L);
            current = current.plusDays(1);
        }

        for (Password password : passwords) {
            if (password.getLastUsed() != null && password.getLastUsed().isAfter(startDate)) {
                String date = password.getLastUsed().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                trend.put(date, trend.getOrDefault(date, 0L) + 1);
            }
        }

        return trend;
    }
    
    public List<String> getMostViewedPasswords(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
        return passwordService.getMostViewedPasswordsByUser(user, 3)
                .stream()
                .map(Password::getTitle)
                .collect(Collectors.toList());
    }
    

    public List<String> getFeaturedPasswords(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
        return passwordService.getFeaturedPasswordsByUser(user)
                .stream()
                .map(Password::getTitle)
                .collect(Collectors.toList());
    }
}