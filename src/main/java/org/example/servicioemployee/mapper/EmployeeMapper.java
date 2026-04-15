package org.example.servicioemployee.mapper;

import org.example.servicioemployee.dto.Response;
import org.example.servicioemployee.model.Employee;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    Response toResponse(Employee entity);
    List<Response> toResponseList(List<Employee> entities);
}
