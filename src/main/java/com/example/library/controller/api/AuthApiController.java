package com.example.library.controller.api;

import com.example.library.entity.User;
import com.example.library.service.AuthService;
import com.example.library.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static com.example.library.controller.api.ApiDtos.*;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthService authService;
    private final UserService userService;
    private final ApiMapper mapper;

    public AuthApiController(AuthService authService, UserService userService, ApiMapper mapper) {
        this.authService = authService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        User user = authService.login(request.email().trim(), request.password())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "ایمیل یا رمز عبور اشتباه است."
                ));
        saveUserInSession(session, user);
        return new AuthResponse(true, mapper.toUser(user));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request, HttpSession session) {
        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim());
        user.setPassword(request.password());
        User savedUser = authService.register(user);
        saveUserInSession(session, savedUser);
        return new AuthResponse(true, mapper.toUser(savedUser));
    }

    @GetMapping("/me")
    public AuthResponse me(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return new AuthResponse(false, null);
        }
        try {
            return new AuthResponse(true, mapper.toUser(userService.findById(userId)));
        } catch (RuntimeException exception) {
            session.invalidate();
            return new AuthResponse(false, null);
        }
    }

    @PostMapping("/logout")
    public MessageResponse logout(HttpSession session) {
        session.invalidate();
        return new MessageResponse("با موفقیت از حساب خارج شدید.");
    }

    private void saveUserInSession(HttpSession session, User user) {
        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getFullName());
        session.setAttribute("userRole", user.getRole().name());
    }
}
