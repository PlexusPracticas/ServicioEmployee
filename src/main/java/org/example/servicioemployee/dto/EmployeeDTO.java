package org.example.servicioemployee.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.example.servicioemployee.model.Employee;

import java.util.List;

public final class EmployeeDTO {
    @Getter @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreateRequest {
        @NotBlank(message = "name – Obligatorio. Longitud máxima de 50 caracteres")
        @Size(max = 50)
        private String name;
        @NotBlank(message = "Surname – Obligatorio. Longitud máxima de 150 caracteres")
        @Size(max = 150)
        private String surname;
        @NotBlank(message = "PlexusMail – Obligatorio. Error de formato de email")
        @Email
        @Size(max = 200)
        private String mailPlexus;
        @Size(max = 50, message = "ClientId – Debe ser un alfanumerico entre 0 y 50 caracteres")
        private String clientId;
        @Email
        @Size(max = 200, message = "mailClient - Error de formato de email")
        private String mailClient;
        @Pattern(regexp = "^$|^[0-9]{9}$",
                message = "phoneNumber - El numero de telefono solo puede contener caracteres numericos. Debe tener 9 caracteres de longitud")
        private String phoneNumber;
    }

    @Getter @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "employees",
            "totalPages",
            "totalElements"
    })

    public static class ListResponse {
        private List<Response> employees;
        private int totalPages;
        private long totalElements;
    }

    @Getter @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DeleteResponse {
        private List<Integer> deleted;
        private List<Integer> notDeleted;

        public DeleteResponse(List<Integer>deleted,List<Integer>notDeleted){
            this.deleted=deleted;
            this.notDeleted=notDeleted;
        }

    }


}
