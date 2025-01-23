package com.example.buttondemo.controller;

import com.example.buttondemo.model.User;
import com.example.buttondemo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        // Eğer kullanıcı zaten giriş yapmışsa, ana sayfaya yönlendir
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/api/gpu";
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        // Validasyon hataları varsa
        if (result.hasErrors()) {
            return "register";
        }

        // Kullanıcı adı kontrolü
        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Bu kullanıcı adı zaten kullanılıyor");
            return "register";
        }

        // Email kontrolü
        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Bu email adresi zaten kullanılıyor");
            return "register";
        }

        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Kayıt başarılı! Lütfen giriş yapın.");
            return "redirect:/login";
        } catch (Exception e) {
            result.rejectValue("username", "error.user", "Kayıt sırasında bir hata oluştu");
            return "register";
        }
    }
} 