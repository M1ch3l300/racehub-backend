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
            String identifier = request.getUsername() != null ? request.getUsername() : request.getEmail();
            User user = null;

            // Prova prima con email
            if (request.getEmail() != null) {
                user = userService.findByEmail(request.getEmail());
            }

            // Se non trovato con email, prova con username
            if (user == null && request.getUsername() != null) {
                user = userService.findByUsername(request.getUsername());
            }

            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Credenziali non valide");
                return ResponseEntity.badRequest().body(error);
            }

            // Autentica con username (interno)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(token, user));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email/Username o password non corretti");
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
