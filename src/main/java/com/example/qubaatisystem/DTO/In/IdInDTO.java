package com.example.qubaatisystem.DTO.In;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic body for "by-id" operations (get-by-id / delete) in the body-based, path-variable-free controller
 * style. {@code id} is a resource target, never an acting-profile id (the actor comes from Basic Auth).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdInDTO {

    @NotNull(message = "id is required")
    private Integer id;
}
