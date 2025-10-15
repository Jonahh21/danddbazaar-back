package com.dandbazaar.back.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dandbazaar.back.auth.services.UserService;

import lombok.Getter;
import lombok.Setter;

import com.dandbazaar.back.auth.entities.User;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserService userDetailsService;

    public AuthController(AuthenticationManager authManager, JwtService jwtService, UserService userDetailsService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User newUser = userDetailsService.register(request.getUsername(), request.getPassword(), request.getEmail());
        String token = jwtService.generateToken(newUser);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        var user = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(token);
    }
}

@Getter @Setter
class AuthRequest {
    private String username;
    private String password;
}

@Getter @Setter
class RegisterRequest {
    private String username;
    private String password;
    private String email;
}
