package com.bookingProject.tour.exp.service.imp;

import com.bookingProject.tour.exp.entity.Characteristic;
import com.bookingProject.tour.exp.entity.Product;
import com.bookingProject.tour.exp.entity.dto.characteristc.SaveCharacteristic;
import com.bookingProject.tour.exp.repository.ICharacteristicRepository;
import com.bookingProject.tour.exp.repository.IProductRepository;
import com.bookingProject.tour.exp.service.ICharacteristicService;
import com.bookingProject.tour.exp.entity.dto.characteristc.CharacteristicDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CharacteristicService implements ICharacteristicService {
    @Autowired
    private ICharacteristicRepository characteristicRepository;
    @Autowired
    private SubirImagen image;
    @Autowired
    private IProductRepository productRepository;

    @Override
    public ResponseEntity<?> guardarCharacter(SaveCharacteristic saveCharacteristic) throws JsonProcessingException {
        List<String> result= image.subir(List.of(saveCharacteristic.getIcon()));
        if(result==null) return new ResponseEntity<>("Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg",HttpStatus.BAD_REQUEST);
        Characteristic characteristic= new Characteristic();
        characteristic.setName(saveCharacteristic.getName());
        characteristic.setId(characteristicRepository.save(characteristic).getId());
        characteristic.setIcon(result.get(0));
        characteristicRepository.save(characteristic);
        return new ResponseEntity<>(characteristic, HttpStatus.CREATED);
    }

    @Override
    public List<Characteristic> traertodo() {
        return characteristicRepository.findAll();
    }

    @Override
    public ResponseEntity<?> traerId(Long id) {
        Optional<Characteristic> find= characteristicRepository.findById(id);
        if (find.isPresent()) return new ResponseEntity<>(find.get(),HttpStatus.OK);
        return new ResponseEntity<>("No se encontró el id en la base de datos",HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> modificarCharacter(CharacteristicDTO characteristicDTO) throws JsonProcessingException {
        Optional<Characteristic> characteristicOptional= characteristicRepository.findById(characteristicDTO.getId());
        if (characteristicOptional.isEmpty()) return new ResponseEntity<>("No se encontró la característica en nuestra base de datos.",HttpStatus.NOT_FOUND);
        List<String> result= image.subir(List.of(characteristicDTO.getIcon()));
        if(result==null) return new ResponseEntity<>("Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg",HttpStatus.BAD_REQUEST);
        Characteristic characteristic= characteristicOptional.get();
        characteristic.setName(characteristicDTO.getName());
        characteristic.setIcon(result.get(0));
        characteristicRepository.save(characteristic);
        return new ResponseEntity<>(characteristic,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> parcialMod(CharacteristicDTO characteristicDTO) throws JsonProcessingException {
        Optional<Characteristic> characteristicOptional= characteristicRepository.findById(characteristicDTO.getId());
        if (characteristicOptional.isEmpty()) return new ResponseEntity<>("No se encontró la característica en nuestra base de datos.",HttpStatus.NOT_FOUND);
        Characteristic characteristic= characteristicOptional.get();
        if (characteristicDTO.getName()!=null) {
            characteristic.setName(characteristicDTO.getName());
            characteristicRepository.save(characteristic);
        }
        if (characteristicDTO.getIcon()!=null&&characteristicDTO.getIcon().getSize()==0) {
            List<String> result= image.subir(List.of(characteristicDTO.getIcon()));
            if (result==null) return new ResponseEntity<>("Solo aceptamos archivos con extensiones jpg, png, gif, jpeg y svg",HttpStatus.BAD_REQUEST);
            characteristic.setIcon(result.get(0));
            characteristicRepository.save(characteristic);
        }
        return new ResponseEntity<>(characteristic,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> eliminar(Long id) {
        Optional<Characteristic> find= characteristicRepository.findById(id);
        if (find.isPresent()) {
            List<Product> products= productRepository.findByCharacterId(id);
            if (!products.isEmpty()){
                for (Product product : products){
                    product.getCharacteristics().remove(find.get());
                    productRepository.save(product);
                }
            }
            characteristicRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else return new ResponseEntity<>("No se encontró la caracteristica en la base de datos", HttpStatus.NOT_FOUND);
    }
}
