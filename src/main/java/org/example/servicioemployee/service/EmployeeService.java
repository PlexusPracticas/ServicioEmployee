package org.example.servicioemployee.service;
import org.example.servicioemployee.dto.EmployeeDTO.CreateRequest;
import org.example.servicioemployee.dto.EmployeeDTO.DeleteResponse;
import org.example.servicioemployee.dto.UpdateRequest;
import org.example.servicioemployee.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    Page<Employee> getAll(Pageable pageable);
    Employee findById(Integer id);
    Employee save(CreateRequest request);
    List<Employee> saveAll(List<CreateRequest>request);
    Employee updateEmployee(UpdateRequest request);
    DeleteResponse deleteById(List<Integer> employeesId);
    Page<Employee> search(String name, String surname, Pageable pageable);
}