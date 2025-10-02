package com.example.demo.controller;

import com.example.demo.dto.CategoryDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @GetMapping
    public String list(Model model){
        // model.addAttribute("items", categoryService.findAll());
        return "admin/category/list";
    }

    @GetMapping("/create")
    public String createForm(Model model){
        model.addAttribute("dto", new CategoryDTO());
        return "admin/category/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("dto") CategoryDTO dto, BindingResult br){
        if (br.hasErrors()) return "admin/category/form";
        // categoryService.save(dto);
        return "redirect:/admin/categories";
    }
}
