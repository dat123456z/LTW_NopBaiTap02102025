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
 return "auth/register";
}

@PostMapping("/register")
public String submit(@Valid @ModelAttribute("dto") RegisterDto dto,
                    BindingResult br,
                    Model model) {
 // 1) Lỗi validate cơ bản
 if (br.hasErrors()) return "auth/register";

 // 2) Kiểm tra confirm password
 if (!dto.getPassword().equals(dto.getConfirmPassword())) {
   br.rejectValue("confirmPassword", "mismatch", "Mật khẩu xác nhận không khớp");
   return "auth/register";
 }

 // 3) Kiểm tra trùng trước khi lưu (để không ném 500)
 if (userRepo.existsByUsername(dto.getUsername())) {
   br.rejectValue("username", "exists", "Tên đăng nhập đã tồn tại");
   return "auth/register";
 }
 if (userRepo.existsByEmail(dto.getEmail())) {
   br.rejectValue("email", "exists", "Email đã được sử dụng");
   return "auth/register";
 }

 // 4) Lưu & về login
 try {
   userRepo.save(UserEntity.builder()
       .username(dto.getUsername())
       .email(dto.getEmail())
       .password(encoder.encode(dto.getPassword()))
       .role("ROLE_USER")
       .enabled(true)
       .build());
 } catch (DataIntegrityViolationException e) {
   // Phòng khi ràng buộc unique ở DB vẫn ném lỗi
   br.reject("duplicate", "Tên đăng nhập hoặc email đã tồn tại");
   return "auth/register";
 }

 model.addAttribute("success", "Đăng ký thành công, vui lòng đăng nhập!");
 return "login"; // hoặc "auth/login" theo file của bạn
}
}
