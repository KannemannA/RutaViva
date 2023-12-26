package com.bookingProject.tour.exp.entity.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveCategory {
    private String category;
    private String description;
    private MultipartFile thumbnail;
}
