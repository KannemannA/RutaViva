package com.bookingProject.tour.exp.entity.dto.userEntity;

import com.bookingProject.tour.exp.entity.ERole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntityDTO {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String lastName;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private ERole role;
}
