package org.example.servicioemployee.dto;

import lombok.NoArgsConstructor;
import org.example.servicioemployee.dto.EmployeeDTO.CreateRequest;
import jakarta.validation.Valid;

import java.util.List;

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
