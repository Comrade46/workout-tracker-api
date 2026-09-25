package com.workouttracker.security;

import com.workouttracker.model.User;
import com.workouttracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /*
     * Every API request carries a JWT, and the filter needs the user's
     * details for it. The database is remote, so looking the user up on
     * every request adds a round trip to each call. Details are kept for
     * a few minutes instead. Login always goes to the database.
     */
    private static final long TOKEN_USER_CACHE_MS = 5 * 60 * 1000;
    private static final int TOKEN_USER_CACHE_MAX = 1000;

    private record CachedUser(UserDetails details, long expiresAt) {
    }

    private final Map<String, CachedUser> tokenUserCache = new ConcurrentHashMap<>();

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        String identifier = usernameOrEmail == null ? "" : usernameOrEmail.trim();

        // Login accepts username or email. Emails are stored lower-case.
        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier.toLowerCase()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));

        // Convert to Spring Security UserDetails object
        return UserDetailsImpl.build(user);
    }

    /*
     * For requests with a valid JWT (subject = username): same result as
     * loadUserByUsername, cached for a few minutes.
     */
    public UserDetails loadUserForToken(String username) {
        long now = System.currentTimeMillis();

        CachedUser cached = tokenUserCache.get(username);

        if (cached != null && cached.expiresAt() > now) {
            return cached.details();
        }

        UserDetails details = loadUserByUsername(username);

        if (tokenUserCache.size() >= TOKEN_USER_CACHE_MAX) {
            tokenUserCache.clear();
        }

        tokenUserCache.put(username, new CachedUser(details, now + TOKEN_USER_CACHE_MS));

        return details;
    }
}
