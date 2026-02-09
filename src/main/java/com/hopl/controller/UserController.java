package com.hopl.controller;

import com.hopl.model.User;
import com.hopl.security.JwtTokenProvider;
import com.hopl.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    public UserController(UserService userService, JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Returns the authenticated user's profile.
     *
     * @param request HTTP request with JWT
     * @return user profile data
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getProfile(HttpServletRequest request) {
        Long userId = extractUserId(request);
        return userService.findById(userId)
                .map(user -> ResponseEntity.ok(toProfileMap(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    private Map<String, Object> toProfileMap(User user) {
        return Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName() != null ? user.getName() : "",
                "plan", user.getPlanType().name(),
                "credits", user.getCredits(),
                "createdAt", user.getCreatedAt().toString()
        );
    }

    private Long extractUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return tokenProvider.getUserIdFromToken(header.substring(7));
        }
        throw new RuntimeException("Authentication required");
    }
}
