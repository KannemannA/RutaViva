package com.bookingProject.tour.exp.service.imp;

import com.bookingProject.tour.exp.entity.Politic;
import com.bookingProject.tour.exp.entity.dto.politic.SavePolitic;
import com.bookingProject.tour.exp.repository.IPoliticRepository;
import com.bookingProject.tour.exp.service.IPoliticService;
import com.bookingProject.tour.exp.entity.dto.politic.PoliticDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PoliticService implements IPoliticService {

    @Autowired
    private IPoliticRepository politicRepository;

    @Override
    public ResponseEntity<?> crearPolitica(SavePolitic savePolitic) {
        Politic politic = Politic.builder()
                .title(savePolitic.getTitle())
                .description(savePolitic.getDescription()).build();
        politicRepository.save(politic);
        return new ResponseEntity<>(politic, HttpStatus.CREATED);
    }

    @Override
    public List<Politic> traerPolitica() {return politicRepository.findAll(); }

    @Override
    public ResponseEntity<?> traerId(Long id) {
        Optional<Politic> find = politicRepository.findById(id);
        if(find.isPresent()) return new ResponseEntity<>(find.get(),HttpStatus.OK);
        return new ResponseEntity<>("No se encontró la politica en la base de datos.", HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> modificarPolitica(PoliticDTO politicDTO) {
        Optional<Politic> find = politicRepository.findById(politicDTO.getId());
        if(find.isPresent()){
            Politic politic = find.get();
            politic.setTitle(politicDTO.getTitle());
            politic.setDescription(politicDTO.getDescription());
            politicRepository.save(politic);
            return new ResponseEntity<>(politic, HttpStatus.OK);
        }
        return new ResponseEntity<>("No se encontró la politica en la base de datos.", HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> parcialMod(PoliticDTO politicDTO) {
        Optional<Politic> find = politicRepository.findById(politicDTO.getId());
        if(find.isPresent()){
            Politic politic = find.get();
            if(politicDTO.getTitle()!=null){
                politic.setTitle(politicDTO.getTitle());
                politicRepository.save(politic);
            }
            if(politicDTO.getDescription()!=null) politic.setDescription(politicDTO.getDescription());
            politicRepository.save(politic);
            return new ResponseEntity<>(politic, HttpStatus.OK);
        }else return new ResponseEntity<>("No se encontró la politica en la base de datos.", HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> eliminarPolitica(Long id) {
        Optional<Politic> politic = politicRepository.findById(id);
        if(politic.isPresent()){
            politicRepository.deleteById(politic.get().getId());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("No se encontró la politica en la base de datos.", HttpStatus.NOT_FOUND);
    }
}
