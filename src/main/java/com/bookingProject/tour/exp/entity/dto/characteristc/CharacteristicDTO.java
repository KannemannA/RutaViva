package com.bookingProject.tour.exp.entity.dto.characteristc;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacteristicDTO {
    private Long id;
    private String name;
    private MultipartFile icon;
}
