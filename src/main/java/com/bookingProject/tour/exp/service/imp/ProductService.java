package com.bookingProject.tour.exp.service.imp;

import com.bookingProject.tour.exp.entity.Category;
import com.bookingProject.tour.exp.entity.Characteristic;
import com.bookingProject.tour.exp.entity.Politic;
import com.bookingProject.tour.exp.entity.Product;
import com.bookingProject.tour.exp.entity.dto.product.SaveProduct;
import com.bookingProject.tour.exp.repository.IPoliticRepository;
import com.bookingProject.tour.exp.repository.IProductRepository;
import com.bookingProject.tour.exp.service.*;
import com.bookingProject.tour.exp.entity.dto.product.ProductDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements IProductService {
    @Autowired
    private IProductRepository productsRepository;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IPoliticRepository politicRepository;
    @Autowired
    private ICharacteristicService characteristicService;
    @Autowired
    private SubirImagen image;

    @Override
    public ResponseEntity<?> guardarProd(SaveProduct saveProduct) throws JsonProcessingException {
        List<String> resultThumb= image.subir(List.of(saveProduct.getThumbnail()));
        List<String> resultImage= image.subir(saveProduct.getImages());
        if(resultThumb==null || resultImage==null) return new ResponseEntity<>("Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg",HttpStatus.BAD_REQUEST);
        Product product = new Product();
        product.setTitle(saveProduct.getTitle());
        product.setId(productsRepository.save(product).getId());
        product.setDescription(saveProduct.getDescription());
        product.setThumbnail(resultThumb.get(0));
        product.setImages(resultImage);
        if (saveProduct.getCategoryId() != null) {
            ResponseEntity<?> responseEntity= categoryService.traerId(saveProduct.getCategoryId());
            if (responseEntity.getBody() instanceof Category) product.setCategory((Category) responseEntity.getBody());
            else throw new RuntimeException("Error al cargar la categoria");
        }
        if (saveProduct.getCharacteristicIds()!=null) {
            List<Characteristic> characteristicList= new ArrayList<>();
            for (Long id: saveProduct.getCharacteristicIds()) {
                ResponseEntity<?> responseEntity = characteristicService.traerId(id);
                if (responseEntity.getBody() instanceof Characteristic) {
                    characteristicList.add((Characteristic) responseEntity.getBody());
                }else throw new RuntimeException("Error al cargar la caracteristica");
            }
            product.setCharacteristics(characteristicList);
        }
        if (saveProduct.getPoliticIds()!=null) {
            for (Long id: saveProduct.getPoliticIds()) {
                Optional<Politic> findp= politicRepository.findById(id);
                if(findp.isEmpty()) return new ResponseEntity<>("No se encontro el id en nuestra bd.", HttpStatus.NOT_FOUND);
                Politic p= findp.get();
                p.setProduct(product);
                politicRepository.save(p);
                product.getPolitics().add(p);
            }
        }
        productsRepository.save(product);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @Override
    public List<Product> traerTodo(){
        return productsRepository.findAll();
    }

    @Override
    public ResponseEntity<?> buscarProd(Long id) {
        Optional<Product> resultado=productsRepository.findById(id);
        if (resultado.isPresent()){
            Product product= resultado.get();
            return new ResponseEntity<>(product,HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No se encontró el id en la base de datos", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> buscarPorCategory(Long id){
        return categoryService.filtrarProductos(id);
    }

    @Override
    public ResponseEntity<?> modificarProd(ProductDTO productDTO) throws JsonProcessingException {
        if (productDTO.getId() == null) return new ResponseEntity<>("Falta el ID y algunos datos para completar la modificación", HttpStatus.BAD_REQUEST);
        Optional<Product> prod= productsRepository.findById(productDTO.getId());
        if (prod.isEmpty()) return new ResponseEntity<>("No se encontró el ID del producto en nuestra base de datos",HttpStatus.NOT_FOUND);
        List<String> resultThumb= image.subir(List.of(productDTO.getThumbnail()));
        List<String> resultImage= image.subir(productDTO.getImages());
        if(resultThumb==null || resultImage==null) return new ResponseEntity<>("Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg",HttpStatus.BAD_REQUEST);
        Product product = prod.get();
        product.setTitle(productDTO.getTitle());
        productsRepository.save(product);
        product.setDescription(productDTO.getDescription());
        product.setThumbnail(resultThumb.get(0));
        product.setImages(resultImage);
        if (productDTO.getCategoryId() != null) {
            ResponseEntity<?> responseEntity= categoryService.traerId(productDTO.getCategoryId());
            if (responseEntity.getBody() instanceof Category) product.setCategory((Category) responseEntity.getBody());
            else product.setCategory(null);
        } else product.setCategory(null);
        if (productDTO.getCharacteristicIds()!=null) {
            List<Characteristic> characteristicList = new ArrayList<>();
            for (Long id : productDTO.getCharacteristicIds()) {
            ResponseEntity<?> responseEntity = characteristicService.traerId(id);
                if (responseEntity.getBody() instanceof Characteristic) {
                characteristicList.add((Characteristic) responseEntity.getBody());
                } else characteristicList.add(null);
            }
            product.setCharacteristics(characteristicList);
        } else product.setCharacteristics(null);
        if (productDTO.getPoliticIds()!=null) {
            for (Long id: productDTO.getPoliticIds()) {
                Optional<Politic> findp= politicRepository.findById(id);
                if(findp.isEmpty()) return new ResponseEntity<>("No se encontro el id en nuestra bd.", HttpStatus.NOT_FOUND);
                Politic p= findp.get();
                p.setProduct(product);
                politicRepository.save(p);
                product.getPolitics().add(p);
            }
        }
        productsRepository.save(product);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> parcialMod(ProductDTO productDTO) throws JsonProcessingException {
        if (productDTO.getId() == null) return new ResponseEntity<>("Falta el ID y algunos datos para completar la modificación", HttpStatus.BAD_REQUEST);
        Optional<Product> prod= productsRepository.findById(productDTO.getId());
        if (prod.isEmpty()) return new ResponseEntity<>("No se encontró el ID del producto en nuestra base de datos",HttpStatus.NOT_FOUND);
        Product product = prod.get();
        if (productDTO.getTitle()!=null){
            product.setTitle(productDTO.getTitle());
            productsRepository.save(product);
        }
        if (productDTO.getDescription()!=null) product.setDescription(productDTO.getDescription());
        if (productDTO.getThumbnail() !=null) {
            List<String> resultThumb= image.subir(List.of(productDTO.getThumbnail()));
            if(resultThumb==null) return new ResponseEntity<>("Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg",HttpStatus.BAD_REQUEST);
            product.setThumbnail(resultThumb.get(0));
        }
        if (productDTO.getImages() !=null) {
            List<String> resultImage= image.subir(productDTO.getImages());
            if(resultImage==null) return new ResponseEntity<>("Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg",HttpStatus.BAD_REQUEST);
            product.setImages(resultImage);
        }
        if (productDTO.getCategoryId()!=null) {
            ResponseEntity<?> responseEntity= categoryService.traerId(productDTO.getCategoryId());
            if (responseEntity.getBody() instanceof Category) product.setCategory((Category) responseEntity.getBody());
            else product.setCategory(null);
        }
        if (productDTO.getCharacteristicIds()!=null) {
            List<Characteristic> characteristicList = new ArrayList<>();
            for (Long id : productDTO.getCharacteristicIds()) {
                ResponseEntity<?> responseEntity = characteristicService.traerId(id);
                if (responseEntity.getBody() instanceof Characteristic) {
                    characteristicList.add((Characteristic) responseEntity.getBody());
                }
            }
            product.setCharacteristics(characteristicList);
        }
        if (productDTO.getPoliticIds()!=null) {
            for (Long id: productDTO.getPoliticIds()) {
                Optional<Politic> findp= politicRepository.findById(id);
                if(findp.isEmpty()) return new ResponseEntity<>("No se encontro el id en nuestra bd.", HttpStatus.NOT_FOUND);
                Politic p= findp.get();
                p.setProduct(product);
                politicRepository.save(p);
                product.getPolitics().add(p);
            }
        }
        productsRepository.save(product);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> valoracion(Long idUser, int rating, Long idProduct) {
        return null;
    }

    @Override
    public ResponseEntity<?> eliminarProd(Long id) {
        Optional<Product> find = productsRepository.findById(id);
        if (find.isPresent()) {
            Product product= find.get();
            for (Politic p : product.getPolitics()){
                politicRepository.deleteById(p.getId());
            }
            productsRepository.deleteById(id);
            return new ResponseEntity<>("",HttpStatus.OK);
        }
        return new ResponseEntity<>("No se encontro el producto en nuestra bd.",HttpStatus.NOT_FOUND);
    }
}
