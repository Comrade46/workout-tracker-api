package com.workouttracker.service;

import com.workouttracker.dto.AccountDTO;
import com.workouttracker.dto.ChangePasswordRequestDTO;
import com.workouttracker.dto.FeedbackDTO;
import com.workouttracker.dto.FeedbackRequestDTO;
import com.workouttracker.dto.JwtResponseDTO;
import com.workouttracker.dto.PasswordHelpRequestDTO;
import com.workouttracker.dto.PasswordResetRequestDTO;
import com.workouttracker.dto.TemporaryPasswordDTO;
import com.workouttracker.exception.ResourceNotFoundException;
import com.workouttracker.model.Feedback;
import com.workouttracker.model.PasswordResetRequest;
import com.workouttracker.model.User;
import com.workouttracker.repository.FeedbackRepository;
import com.workouttracker.repository.PasswordResetRequestRepository;
import com.workouttracker.repository.UserRepository;
import com.workouttracker.repository.WorkoutSessionRepository;
import com.workouttracker.security.AdminAccess;
import com.workouttracker.security.JwtUtils;
import com.workouttracker.security.UserDetailsServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
 * Account self-service (profile, change password, feedback) and the
 * admin tools (users, forgot-password requests, temporary passwords).
 */
@Service
public class AccountService {

    // No 0/O, 1/l/I: easy to read out or type from a chat message
    private static final String TEMP_PASSWORD_CHARS = "abcdefghjkmnpqrstuvwxyz23456789";
    private static final int TEMP_PASSWORD_GROUPS = 3;
    private static final int TEMP_PASSWORD_GROUP_LENGTH = 4;

    private final UserRepository userRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final PasswordResetRequestRepository resetRequestRepository;
    private final FeedbackRepository feedbackRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AdminAccess adminAccess;
    private final UserDetailsServiceImpl userDetailsService;
    private final SecureRandom random = new SecureRandom();

    public AccountService(
            UserRepository userRepository,
            WorkoutSessionRepository workoutSessionRepository,
            PasswordResetRequestRepository resetRequestRepository,
            FeedbackRepository feedbackRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils,
            AdminAccess adminAccess,
            UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.workoutSessionRepository = workoutSessionRepository;
        this.resetRequestRepository = resetRequestRepository;
        this.feedbackRepository = feedbackRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.adminAccess = adminAccess;
        this.userDetailsService = userDetailsService;
    }

    // =========================================================
    // LOGIN TOKENS
    // =========================================================

    // New login token + account flags for the app.
    @Transactional(readOnly = true)
    public JwtResponseDTO issueToken(String username) {
        return issueToken(findUser(username));
    }

    private JwtResponseDTO issueToken(User user) {
        return new JwtResponseDTO(
                jwtUtils.generateJwtToken(user.getUsername()),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                adminAccess.isAdmin(user.getUsername()),
                user.isMustChangePassword());
    }

    // =========================================================
    // MY ACCOUNT
    // =========================================================

    @Transactional(readOnly = true)
    public AccountDTO getAccount(String username) {
        return toAccount(findUser(username), null, null);
    }

    /*
     * Checks the current password, saves the new one and returns a fresh
     * token. Tokens issued before the change (other phones) stop working.
     */
    @Transactional
    public JwtResponseDTO changePassword(String username, ChangePasswordRequestDTO request) {
        User user = findUser(username);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is not correct.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("The new password must be different from the current one.");
        }

        setPassword(user, request.getNewPassword(), false);

        // An open "forgot password" request is no longer needed.
        resetRequestRepository.findByUserIdAndResolvedAtIsNull(user.getId())
                .forEach(open -> open.setResolvedAt(LocalDateTime.now()));

        return issueToken(user);
    }

    // =========================================================
    // FORGOT PASSWORD (login page, no login needed)
    // =========================================================

    /*
     * Records a request for the admin. Always "succeeds" so the form
     * never reveals whether an account exists.
     */
    @Transactional
    public void requestPasswordHelp(PasswordHelpRequestDTO request) {
        String identifier = request.getUsernameOrEmail().trim();
        String message = clean(request.getMessage(), 300);

        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier.toLowerCase()))
                .orElse(null);

        if (user == null) {
            return;
        }

        // One open request per user: a second one just refreshes it.
        PasswordResetRequest open = resetRequestRepository
                .findFirstByUserIdAndResolvedAtIsNull(user.getId())
                .orElse(null);

        if (open != null) {
            open.setMessage(message);
            open.setCreatedAt(LocalDateTime.now());
            return;
        }

        resetRequestRepository.save(new PasswordResetRequest(user.getId(), message));
    }

    // =========================================================
    // FEEDBACK
    // =========================================================

    @Transactional
    public void saveFeedback(Long userId, FeedbackRequestDTO request) {
        feedbackRepository.save(new Feedback(
                userId,
                request.getMessage().trim(),
                clean(request.getAppVersion(), 20),
                clean(request.getPage(), 200),
                clean(request.getDevice(), 300)));
    }

    // =========================================================
    // ADMIN
    // =========================================================

    @Transactional(readOnly = true)
    public List<AccountDTO> listUsers() {
        Map<Long, Object[]> stats = new HashMap<>();

        for (Object[] row : workoutSessionRepository.countByUser()) {
            stats.put((Long) row[0], row);
        }

        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getUsername, String.CASE_INSENSITIVE_ORDER))
                .map(user -> {
                    Object[] row = stats.get(user.getId());
                    return toAccount(
                            user,
                            row == null ? 0L : (Long) row[1],
                            row == null ? null : (LocalDate) row[2]);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PasswordResetRequestDTO> openPasswordRequests() {
        List<PasswordResetRequest> requests =
                resetRequestRepository.findByResolvedAtIsNullOrderByCreatedAtDesc();

        Map<Long, User> users = userRepository
                .findAllById(requests.stream().map(PasswordResetRequest::getUserId).toList())
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return requests.stream()
                .filter(request -> users.containsKey(request.getUserId()))
                .map(request -> {
                    User user = users.get(request.getUserId());
                    return new PasswordResetRequestDTO(
                            request.getId(),
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            request.getMessage(),
                            request.getCreatedAt());
                })
                .collect(Collectors.toList());
    }

    /*
     * Gives the user a new random password that must be changed at the
     * next login, and closes their open requests. The password is shown
     * to the admin only in this response.
     */
    @Transactional
    public TemporaryPasswordDTO createTemporaryPassword(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        String temporaryPassword = randomPassword();

        setPassword(user, temporaryPassword, true);

        resetRequestRepository.findByUserIdAndResolvedAtIsNull(user.getId())
                .forEach(open -> open.setResolvedAt(LocalDateTime.now()));

        return new TemporaryPasswordDTO(user.getId(), user.getUsername(), temporaryPassword);
    }

    // Close a request without changing the password (e.g. spam).
    @Transactional
    public void dismissPasswordRequest(Long requestId) {
        PasswordResetRequest request = resetRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + requestId));

        if (request.getResolvedAt() == null) {
            request.setResolvedAt(LocalDateTime.now());
        }
    }

    @Transactional(readOnly = true)
    public List<FeedbackDTO> listFeedback() {
        List<Feedback> items = feedbackRepository.findTop200ByOrderByCreatedAtDesc();

        Map<Long, String> usernames = userRepository
                .findAllById(items.stream().map(Feedback::getUserId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        return items.stream()
                .map(item -> new FeedbackDTO(
                        item.getId(),
                        usernames.getOrDefault(item.getUserId(), "(deleted user)"),
                        item.getMessage(),
                        item.getAppVersion(),
                        item.getPage(),
                        item.getDevice(),
                        item.getCreatedAt(),
                        item.isResolved()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void setFeedbackResolved(Long feedbackId, boolean resolved) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with ID: " + feedbackId));

        feedback.setResolved(resolved);
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private void setPassword(User user, String rawPassword, boolean mustChange) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setMustChangePassword(mustChange);

        // Whole seconds, like the token's issued-at time
        user.setPasswordChangedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        userRepository.save(user);

        // Drop the cached login details once the change is committed.
        String username = user.getUsername();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                userDetailsService.evict(username);
            }
        });
    }

    private String randomPassword() {
        StringBuilder password = new StringBuilder();

        for (int group = 0; group < TEMP_PASSWORD_GROUPS; group++) {
            if (group > 0) {
                password.append('-');
            }

            for (int i = 0; i < TEMP_PASSWORD_GROUP_LENGTH; i++) {
                password.append(TEMP_PASSWORD_CHARS.charAt(random.nextInt(TEMP_PASSWORD_CHARS.length())));
            }
        }

        return password.toString();
    }

    private AccountDTO toAccount(User user, Long workoutCount, LocalDate lastWorkoutDate) {
        return new AccountDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                adminAccess.isAdmin(user.getUsername()),
                user.isMustChangePassword(),
                workoutCount,
                lastWorkoutDate);
    }

    private static String clean(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.length() > maxLength ? trimmed.substring(0, maxLength) : trimmed;
    }
}
