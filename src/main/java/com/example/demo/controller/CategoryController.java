package com.example.demo.controller;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepo;
import com.example.demo.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepo categoryRepo;
    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", categoryRepo.findAll());
        return "admin/categories";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("category", new Category()); 
        return "admin/category/category-form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("category") Category category,
                         RedirectAttributes ra) {
        categoryService.save(category); 
        ra.addFlashAttribute("success", "Thêm danh mục thành công!");
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryRepo.findById(id).orElseThrow());
        return "admin/category/category-form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @ModelAttribute("category") Category form,
                       BindingResult br,
                       RedirectAttributes ra,
                       Model model) {

        // 1) Tồn tại?
        Category entity = categoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 2) Validate cơ bản
        if (form.getName() == null || form.getName().isBlank()) {
            br.rejectValue("name", "blank", "Tên danh mục không được trống");
        }
        if (br.hasErrors()) {
            // Trả về đúng view form
            model.addAttribute("category", form);
            return "admin/category/category-form";
        }

        // 3) Check trùng tên (trừ chính nó)
        if (categoryRepo.existsByNameAndIdNot(form.getName(), id)) {
            br.rejectValue("name", "duplicate", "Tên danh mục đã tồn tại");
            model.addAttribute("category", form);
            return "admin/category/category-form";
        }

        // 4) Cập nhật & lưu
        entity.setName(form.getName());
        entity.setDescription(form.getDescription());
        try {
            categoryRepo.save(entity);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            br.reject("db", "Dữ liệu không hợp lệ (có thể trùng tên hoặc vi phạm ràng buộc)");
            model.addAttribute("category", form);
            return "admin/category/category-form";
        }

        ra.addFlashAttribute("success", "Cập nhật danh mục thành công!");
        return "redirect:/admin/categories";
    }


    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        categoryRepo.deleteById(id);
        ra.addFlashAttribute("success", "Xóa danh mục thành công!");
        return "redirect:/admin/categories";
    }
}

