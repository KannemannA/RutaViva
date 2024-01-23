package com.bookingProject.tour.exp.service.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
public class SubirImagen {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;

    public List<String> subir(List<MultipartFile> imagenes) throws JsonProcessingException {
        int val= 0;
        for (MultipartFile file : imagenes){
            String ext= Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")+1);
            switch (ext){
                case "jpg", "png", "gif", "jpeg", "svg":
                    break;
                default:
                    val++;
            }
        }
        if (val ==0){
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            for(MultipartFile image: imagenes){
                body.add("images",image.getResource());
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity("https://project-1.alejokannemann.com.ar/v1/images/upload", requestEntity, String.class);
            return mapper.readValue(response.getBody(), new TypeReference<>() {});
        }
        return null;
    }
}
