package com.bookingProject.tour.exp.service;

import com.bookingProject.tour.exp.entity.Product;
import com.bookingProject.tour.exp.entity.dto.product.ProductDTO;
import com.bookingProject.tour.exp.entity.dto.product.SaveProduct;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IProductService {
    public ResponseEntity<?> guardarProd(SaveProduct saveProduct) throws JsonProcessingException;
    public List<Product> traerTodo();
    public ResponseEntity<?> buscarProd(Long id);
    public ResponseEntity<?> buscarPorCategory(Long id);
    public ResponseEntity<?> modificarProd(ProductDTO productDTO) throws JsonProcessingException;
    public ResponseEntity<?> parcialMod(ProductDTO productDTO) throws JsonProcessingException;
    public ResponseEntity<?> valoracion(Long idUser, int rating,Long idProduct);
    public ResponseEntity<?> eliminarProd(Long id);
}
