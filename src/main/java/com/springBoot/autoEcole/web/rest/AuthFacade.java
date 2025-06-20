package com.springBoot.autoEcole.web.rest;

import java.security.Principal;
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
import com.springBoot.autoEcole.config.CustomUserDetails;
import com.springBoot.autoEcole.dto.LoginRequest;
import com.springBoot.autoEcole.dto.LoginResponse;
import com.springBoot.autoEcole.model.User;
import com.springBoot.autoEcole.service.UserService;

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

            // Get the CustomUserDetails from authentication
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

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
            User user = userService.findUserByEmail(email);
            return passwordEncoder.matches(password, user.getPassword());
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Debug information
        System.out.println("Authentication: " + authentication);
        System.out.println("Principal: " + (authentication != null ? authentication.getPrincipal() : "null"));
        System.out.println("Is Authenticated: " + (authentication != null ? authentication.isAuthenticated() : "false"));

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser") ||
                authentication.getName().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("No authenticated user");
        }

        try {
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                User user = userDetails.getUser();
                return ResponseEntity.ok(new LoginResponse("User found", user.getEmail(), user.getRole()));
            } else {
                return ResponseEntity.status(401).body("Invalid authentication type");
            }
        } catch (Exception e) {
            System.out.println("Exception in getCurrentUser: " + e.getMessage());
            return ResponseEntity.status(401).body("User not found");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("Logout called - Authentication before logout: " + authentication);

        if (authentication != null) {
            // Use the logout handler
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        // Explicitly clear the security context
        SecurityContextHolder.clearContext();

        // Invalidate the session manually
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("Invalidating session: " + session.getId());
            session.invalidate();
        }

        // Verify the context is cleared
        Authentication afterLogout = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication after logout: " + afterLogout);

        return ResponseEntity.ok(new LoginResponse("Logout successful", null, null));
    }

    @GetMapping("/logout-success")
    public ResponseEntity<?> logoutSuccess() {
        return ResponseEntity.ok(new LoginResponse("Successfully logged out", null, null));
    }

    // Alternative logout that redirects to Spring Security's logout
    @PostMapping("/logout-spring")
    public void logoutSpring(HttpServletResponse response) throws Exception {
        response.sendRedirect("/auth/logout");
    }
}