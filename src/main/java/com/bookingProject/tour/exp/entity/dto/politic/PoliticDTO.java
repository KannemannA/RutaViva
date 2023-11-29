package com.bookingProject.tour.exp.entity.dto.politic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoliticDTO {
    private Long id;
    private String title;
    private String description;
}
