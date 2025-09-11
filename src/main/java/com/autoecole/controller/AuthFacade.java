package com.autoecole.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
import com.autoecole.config.CustomUserDetails;
import com.autoecole.dto.LoginRequest;
import com.autoecole.dto.LoginResponse;
import com.autoecole.model.User;
import com.autoecole.service.UserService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
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

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            return ResponseEntity.ok(new LoginResponse("Login successful", user.getEmail(), user.getRole().name()));

        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(new LoginResponse("Invalid credentials", null, null));
        }
    }

    @GetMapping("/validate/{email}/{password}")
    public boolean validateUser(@PathVariable String email, @PathVariable String password) {
        try {
            User user = userService.findUserByEmail(email);
            return passwordEncoder.matches(password, user.getPassword());
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateUser(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.findUserByEmail(loginRequest.getEmail());
            boolean isValid = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            return ResponseEntity.ok(false); // Don't reveal if user exists or not
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser") ||
                authentication.getName().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("No authenticated user");
        }

        try {
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                User user = userDetails.getUser();
                return ResponseEntity.ok(new LoginResponse("User found", user.getEmail(), user.getRole().name()));
            } else {
                return ResponseEntity.status(401).body("Invalid authentication type");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("User not found");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok(new LoginResponse("Logout successful", null, null));
    }

    /**
     * Logout success endpoint - called automatically by Spring Security after successful logout
     * This provides a consistent JSON response for both manual logout calls and Spring Security redirects
     */
    @GetMapping("/logout-success")
    public ResponseEntity<?> logoutSuccess() {
        return ResponseEntity.ok(new LoginResponse("Successfully logged out", null, null));
    }
}