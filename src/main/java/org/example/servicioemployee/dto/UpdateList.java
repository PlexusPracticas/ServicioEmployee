package org.example.servicioemployee.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;


import java.util.List;


@Getter @Setter
public class UpdateList {

    @Valid
    @NotEmpty(message = "Debe incluir al menos un empleado a actualizar")
    private List<UpdateRequest> employees;   // ✔ correcta
}

