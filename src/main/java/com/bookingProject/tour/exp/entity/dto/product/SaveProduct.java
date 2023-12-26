package com.bookingProject.tour.exp.entity.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SaveProduct {
    private String title;
    private String description;
    private MultipartFile thumbnail;
    private List<MultipartFile> images;
    private Long categoryId;
    private List<Long> characteristicIds;
    private List<Long> politicIds;
}
