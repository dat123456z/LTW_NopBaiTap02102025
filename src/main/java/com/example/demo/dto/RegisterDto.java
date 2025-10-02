package com.example.demo.dto;

//DTO: com.example.demo.dto.RegisterDto
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterDto {
@NotBlank(message = "Tên đăng nhập không được để trống")
@Size(min = 4, max = 20, message = "Tên đăng nhập từ 4–20 ký tự")
private String username;

@NotBlank(message = "Email không được để trống")
@Email(message = "Email không hợp lệ")
private String email;

@NotBlank(message = "Mật khẩu không được để trống")
@Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự")
private String password;

@NotBlank(message = "Xác nhận mật khẩu không được để trống")
private String confirmPassword;
}
