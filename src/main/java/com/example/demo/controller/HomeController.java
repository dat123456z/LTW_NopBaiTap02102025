package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

	@GetMapping("/login")
	public ModelAndView login(Authentication auth) {
		// Nếu đã đăng nhập → điều hướng theo role để tránh ở lại /login
		if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
			var roles = auth.getAuthorities().toString();
			if (roles.contains("ROLE_ADMIN"))
				return new ModelAndView("redirect:/admin/home");
			if (roles.contains("ROLE_USER"))
				return new ModelAndView("redirect:/user/home");
		}
		return new ModelAndView("login"); // Sử dụng ModelAndView để tránh circular reference
	}

	@GetMapping("/admin/home")
	public String adminHome() {
		return "admin/home";
	}

	@GetMapping("/user/home")
	public String userHome() {
		return "user/home";
	}


}