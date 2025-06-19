package com.springBoot.autoEcole.web.rest;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import com.springBoot.autoEcole.dto.LoginRequest;
import com.springBoot.autoEcole.dto.LoginResponse;
import com.springBoot.autoEcole.model.User;
import com.springBoot.autoEcole.service.UserService;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthFacade {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userService.loadUserByUsername(loginRequest.getEmail());
            return ResponseEntity.ok(new LoginResponse("Login successful", user.getEmail(), user.getRole()));

        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(new LoginResponse("Invalid credentials", null, null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(new LoginResponse("User registered successfully", savedUser.getEmail(), savedUser.getRole()));
    }

    @GetMapping("/validate/{email}/{password}")
    public boolean validateUser(@PathVariable String email, @PathVariable String password) {
        try {
            User user = userService.loadUserByUsername(email);
            return passwordEncoder.matches(password, user.getPassword());
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/user")
    public Principal getCurrentUser(HttpServletRequest request) {
        return request.getUserPrincipal();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return ResponseEntity.ok(new LoginResponse("Logout successful", null, null));
    }

    @GetMapping("/logout-success")
    public ResponseEntity<?> logoutSuccess() {
        return ResponseEntity.ok(new LoginResponse("Successfully logged out", null, null));
    }
}