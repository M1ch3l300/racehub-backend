package com.racehub.controller;

import com.racehub.dto.AuthResponse;
import com.racehub.dto.LoginRequest;
import com.racehub.dto.RegisterRequest;
import com.racehub.model.User;
import com.racehub.security.JwtUtil;
import com.racehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setRole(request.getRole());

            User savedUser = userService.registerUser(user);

            // Auto-login dopo registrazione
            UserDetails userDetails = userService.loadUserByUsername(savedUser.getUsername());
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(token, savedUser));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            User user = (User) userDetails;

            return ResponseEntity.ok(new AuthResponse(token, user));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Username o password non corretti");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
