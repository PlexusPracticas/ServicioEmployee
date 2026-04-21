package org.example.servicioemployee.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.servicioemployee.dto.EmployeeDTO.CreateRequest;
import jakarta.validation.Valid;

import java.util.List;
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeBatchRequest {
    @Valid
    private List<CreateRequest> employees;

    public EmployeeBatchRequest() {
    }

    public List<CreateRequest> getEmployees() {
        return employees;
    }

    public void setEmployees(List<CreateRequest> employees) {
        this.employees = employees;
    }
}
