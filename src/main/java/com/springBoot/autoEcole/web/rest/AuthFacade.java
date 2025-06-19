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
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // If authentication successful, set it in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            User user = userService.findUserByEmail(loginRequest.getEmail());

            return ResponseEntity.ok(new LoginResponse("Login successful", user.getEmail(), user.getRole()));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(new LoginResponse("Invalid credentials", null, null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new LoginResponse("Login failed: " + e.getMessage(), null, null));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            User user = userService.findUserByEmail(principal.getName());
            return ResponseEntity.ok(new LoginResponse("User found", user.getEmail(), user.getRole()));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return ResponseEntity.ok(new LoginResponse("Logout successful", null, null));
    }
}