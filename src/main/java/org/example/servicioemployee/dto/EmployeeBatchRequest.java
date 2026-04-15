package org.example.servicioemployee.dto;

import org.example.servicioemployee.dto.EmployeeDTO.CreateRequest;
import jakarta.validation.Valid;

import java.util.List;

public class EmployeeBatchRequest {
    @Valid
    private List<CreateRequest> employees;
    public List<CreateRequest> getEmployees() {
        return employees;
    }
    public void setEmployees(List<CreateRequest> employees) {
        this.employees = employees;
    }
}
