package com.example.qubaatisystem.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Student-safe choice view: id + content only. Deliberately omits scoreImpact (hidden scoring). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentMissionChoiceOutDTO {

    private Integer id;
    private String content;
}
