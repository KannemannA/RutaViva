package com.bookingProject.tour.exp.service.imp;

import com.bookingProject.tour.exp.entity.Category;
import com.bookingProject.tour.exp.entity.Product;
import com.bookingProject.tour.exp.entity.dto.category.SaveCategory;
import com.bookingProject.tour.exp.repository.ICategoryRepository;
import com.bookingProject.tour.exp.repository.IProductRepository;
import com.bookingProject.tour.exp.service.ICategoryService;
import com.bookingProject.tour.exp.entity.dto.category.CategoryDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService implements ICategoryService {
    @Autowired
    private ICategoryRepository categoryRepository;
    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private SubirImagen restImage;

    @Override
    public ResponseEntity<?> crearCategoria(SaveCategory saveCategory) throws JsonProcessingException {
        List<String> result= restImage.subir(List.of(saveCategory.getThumbnail()));
        if(result==null) return new ResponseEntity<>("Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg",HttpStatus.BAD_REQUEST);
        Category category = Category.builder()
                .category(saveCategory.getCategory())
                .description(saveCategory.getDescription())
                .thumbnail(result.get(0))
                .build();
        categoryRepository.save(category);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }


    @Override
    public List<Category> traercategorias() {
        return categoryRepository.findAll();
    }

    @Override
    public ResponseEntity<?> traerId(Long id){
        Optional<Category> find= categoryRepository.findById(id);
        if (find.isPresent()){
            return new ResponseEntity<>(find.get(),HttpStatus.OK);
        }
        return new ResponseEntity<>("No se encontró el ID en nuestra base de datos.",HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> filtrarProductos(Long id) {
        Optional<Category> find= categoryRepository.findById(id);
        if (find.isPresent()) {
            Category category= find.get();
            if (category.getProducts().isEmpty()) return new ResponseEntity<>("No hay productos asociados a esta categoria",HttpStatus.OK);
            return new ResponseEntity<>(category.getProducts(),HttpStatus.OK);
        }
        return new ResponseEntity<>("No se encontró el id en la bd",HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> modificarCategoria(CategoryDTO categoryDTO) throws JsonProcessingException {
        Optional<Category> find = categoryRepository.findById(categoryDTO.getId());
        List<String> result= restImage.subir(List.of(categoryDTO.getThumbnail()));
        if(result==null) return new ResponseEntity<>("Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg",HttpStatus.BAD_REQUEST);
        if (find.isPresent()) {
            Category category = find.get();
            category.setCategory(categoryDTO.getCategory());
            categoryRepository.save(category);
            category.setDescription(categoryDTO.getDescription());
            category.setThumbnail(result.get(0));
            categoryRepository.save(category);
            return new ResponseEntity<>(category, HttpStatus.OK);
        }
        return new ResponseEntity<>("No se encontró la categoría en la base de datos", HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> parcialMod(CategoryDTO categoryDTO) throws JsonProcessingException {
        Optional<Category> find = categoryRepository.findById(categoryDTO.getId());
        if (find.isPresent()) {
            Category category = find.get();
            if (categoryDTO.getThumbnail()!=null&&categoryDTO.getThumbnail().getSize()!=0){
                List<String> result= restImage.subir(List.of(categoryDTO.getThumbnail()));
                if (result==null) return new ResponseEntity<>("Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg",HttpStatus.BAD_REQUEST);
                category.setThumbnail(result.get(0));
            }
            if (categoryDTO.getCategory()!=null){
                category.setCategory(categoryDTO.getCategory());
                categoryRepository.save(category);
            }
            if (categoryDTO.getDescription()!=null) category.setDescription(categoryDTO.getDescription());
            categoryRepository.save(category);
            return new ResponseEntity<>(category, HttpStatus.OK);
        }
        return new ResponseEntity<>("No se encontró la categoría en la base de datos", HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> eliminarCategoria(Long id) {
        Optional<Category> category= categoryRepository.findById(id);
        if (category.isPresent()){
            for (Product p : category.get().getProducts()){
                p.setCategory(null);
                productRepository.save(p);
            }
            categoryRepository.deleteById(category.get().getId());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("No se encontró la categoría en nuestra base de datos",HttpStatus.NOT_FOUND);
    }
}
