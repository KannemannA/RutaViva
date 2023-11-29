package com.bookingProject.tour.exp.entity.dto.product;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductDTO {
    private Long id;
    private String title;
    private String description;
    private MultipartFile thumbnail;
    private List<MultipartFile> images;
    private Long categoryId;
    private List<Long> characteristicIds;
    private List<Long> politicIds;
}
