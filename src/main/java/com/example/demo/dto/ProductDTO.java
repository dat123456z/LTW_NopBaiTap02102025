package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductDTO {
    private Long id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 150, message = "Tên sản phẩm tối đa 150 ký tự")
    private String name;

    @Positive(message = "Giá phải > 0")
    private Double price;

    @NotNull(message = "Phải chọn danh mục")
    private Long categoryId;
}
