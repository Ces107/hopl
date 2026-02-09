package com.hopl.service;

import com.hopl.dto.auth.LoginRequest;
import com.hopl.dto.auth.RegisterRequest;
import com.hopl.dto.auth.TokenResponse;
import com.hopl.exception.ApiException;
import com.hopl.model.User;
import com.hopl.model.enums.PlanType;
import com.hopl.repository.UserRepository;
import com.hopl.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Registers a new user account.
     *
     * @param request registration data
     * @return JWT token response
     */
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already registered", HttpStatus.CONFLICT);
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPlanType(PlanType.FREE);
        user.setCredits(0);
        User saved = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(saved.getId(), saved.getEmail());
        return new TokenResponse(token, jwtTokenProvider.getExpiration());
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request login credentials
     * @return JWT token response
     */
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        return new TokenResponse(token, jwtTokenProvider.getExpiration());
    }
}
