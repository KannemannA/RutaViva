package com.bookingProject.tour.exp.entity.dto.category;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String category;
    private String description;
    private MultipartFile thumbnail;
}
