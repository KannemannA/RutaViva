package com.bookingProject.tour.exp.service;

import com.bookingProject.tour.exp.entity.Characteristic;
import com.bookingProject.tour.exp.entity.dto.characteristc.CharacteristicDTO;
import com.bookingProject.tour.exp.entity.dto.characteristc.SaveCharacteristic;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICharacteristicService {
    public ResponseEntity<?> guardarCharacter(SaveCharacteristic saveCharacteristic) throws JsonProcessingException;
    public List<Characteristic> traertodo();
    public ResponseEntity<?> traerId(Long id);
    public ResponseEntity<?> modificarCharacter(CharacteristicDTO characteristicDTO) throws JsonProcessingException;
    public ResponseEntity<?> parcialMod(CharacteristicDTO characteristicDTO) throws JsonProcessingException;
    public ResponseEntity<?> eliminar(Long id);
}
