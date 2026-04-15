package org.example.servicioemployee.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRequest {
    @NotNull
    private Integer id;
    @Size(max=50,message = "ClientId – Debe ser un alfanumerico entre 0 y 50 caracteres")
    private String clientId;
    @Email(message = "mailClient - Error de formato de email ")
    private String mailClient;
    @Pattern(regexp = "^[0-9]{9}$", message = "phoneNumber - El numero de telefono solo puede contener caracteres numericos. Debe tener 9 caracteres de longitud")
    private String phoneNumber;
}
