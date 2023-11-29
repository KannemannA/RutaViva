package com.bookingProject.tour.exp.entity.dto.characteristc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveCharacteristic {
    private String name;
    private MultipartFile icon;
}
