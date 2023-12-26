package com.bookingProject.tour.exp.service;

import com.bookingProject.tour.exp.entity.Politic;
import com.bookingProject.tour.exp.entity.dto.politic.PoliticDTO;
import com.bookingProject.tour.exp.entity.dto.politic.SavePolitic;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface IPoliticService {

    public ResponseEntity<?> crearPolitica(SavePolitic savePolitic);

    public List<Politic> traerPolitica();

    public ResponseEntity<?> traerId(Long id);

    public ResponseEntity<?> modificarPolitica(PoliticDTO politicDTO);

    public ResponseEntity<?> parcialMod(PoliticDTO politicDTO);

    public ResponseEntity<?> eliminarPolitica(Long id);
}
