package com.example.demo.controller;

//com.example.demo.controller.AuthController
import com.example.demo.dto.RegisterDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

private final UserRepo userRepo;
private final PasswordEncoder encoder;

@GetMapping("/register")
public String form(Model model) {
    model.addAttribute("dto", new RegisterDto());
    return "register"; // file: templates/register.html
}

@PostMapping("/register")
public String submit(@Valid @ModelAttribute("dto") RegisterDto dto,
                     BindingResult br, Model model) {
    if (br.hasErrors()) return "register";
    if (!dto.getPassword().equals(dto.getConfirmPassword())) {
        br.rejectValue("confirmPassword", "mismatch", "Mật khẩu xác nhận không khớp");
        return "register";
    }
    if (userRepo.existsByUsername(dto.getUsername())) {
        br.rejectValue("username", "exists", "Tên đăng nhập đã tồn tại");
        return "register";
    }
    if (userRepo.existsByEmail(dto.getEmail())) {
        br.rejectValue("email", "exists", "Email đã được sử dụng");
        return "register";
    }
    try {
        userRepo.save(UserEntity.builder()
            .username(dto.getUsername())
            .email(dto.getEmail())
            .password(encoder.encode(dto.getPassword()))
            .role("ROLE_USER").enabled(true).build());
    } catch (DataIntegrityViolationException e) {
        br.reject("duplicate", "Tên đăng nhập hoặc email đã tồn tại");
        return "register";
    }
    return "redirect:/login?registerSuccess=true";
}

}
