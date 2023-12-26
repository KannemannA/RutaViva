package com.bookingProject.tour.exp.service;

import com.bookingProject.tour.exp.entity.Category;
import com.bookingProject.tour.exp.entity.dto.category.CategoryDTO;
import com.bookingProject.tour.exp.entity.dto.category.SaveCategory;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICategoryService {
    public ResponseEntity<?> crearCategoria(SaveCategory saveCategory) throws JsonProcessingException;
    public List<Category> traercategorias();
    public ResponseEntity<?> traerId(Long id);
    public ResponseEntity<?> filtrarProductos(Long id);
    public ResponseEntity<?> modificarCategoria(CategoryDTO categoryDTO) throws JsonProcessingException;
    public ResponseEntity<?> parcialMod(CategoryDTO categoryDTO) throws JsonProcessingException;
    public ResponseEntity<?> eliminarCategoria(Long id);
}
