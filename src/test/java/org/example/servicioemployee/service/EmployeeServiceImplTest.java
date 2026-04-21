package org.example.servicioemployee.service;

import org.example.servicioemployee.dto.EmployeeDTO;
import org.example.servicioemployee.dto.UpdateRequest;
import org.example.servicioemployee.model.Employee;
import org.example.servicioemployee.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {
    @Mock
    private EmployeeRepository repository;
    @InjectMocks
    private EmployeeServiceImpl service;


    @Test
    void getAll_ok() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(new Employee())));
        Page<Employee> result = service.getAll(pageable);
        assertEquals(1, result.getTotalElements());
    }


    @Test
    void buscarEmpleado_porId_ok() {
        Employee emp = new Employee();
        emp.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(emp));

        Employee result = service.findById(1);
        assertEquals(1, result.getId());
    }
    @Test
    void buscarEmpleado_noExiste_error() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findById(99));
    }


    @Test
    void crearEmpleado_ok() {
        EmployeeDTO.CreateRequest req = new EmployeeDTO.CreateRequest();
        req.setName("Tony");
        req.setSurname("Stark");
        req.setMailPlexus("tony@plexus.com");

        when(repository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));
        Employee result = service.save(req);

        assertEquals("Tony", result.getName());
        assertEquals("Stark", result.getSurname());
    }


    @Test
    void crearVariosEmpleados_ok() {
        EmployeeDTO.CreateRequest r1 = new EmployeeDTO.CreateRequest();
        r1.setName("Tony");
        EmployeeDTO.CreateRequest r2 = new EmployeeDTO.CreateRequest();
        r2.setName("Bruce");
        when(repository.save(any(Employee.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        List<Employee> result = service.saveAll(List.of(r1, r2));
        assertEquals(2, result.size());
    }


    @Test
    void actualizarEmpleado_ok() {
        Employee emp = new Employee();
        emp.setId(1);
        UpdateRequest req = new UpdateRequest();
        req.setId(1);
        req.setPhoneNumber("699999999");
        when(repository.findById(1)).thenReturn(Optional.of(emp));
        when(repository.save(any(Employee.class))) .thenReturn(emp);
        Employee result = service.updateEmployee(req);
        assertEquals("699999999", result.getPhoneNumber());
    }
    @Test
    void actualizarEmpleado_noExiste_error() {
        UpdateRequest req = new UpdateRequest();
        req.setId(99);
        when(repository.findById(99)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.updateEmployee(req));
    }

    @Test
    void buscarPorNombreYApellido() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase("Tony", "Stark", pageable))
                .thenReturn(new PageImpl<>(List.of(new Employee())));
        Page<Employee> result =service.search("Tony", "Stark", pageable);
        assertEquals(1, result.getTotalElements());
    }
    @Test
    void buscarPorNombre() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findByNameContainingIgnoreCase("Tony", pageable))
                .thenReturn(new PageImpl<>(List.of(new Employee())));
        Page<Employee> result =service.search("Tony", null, pageable);
        assertEquals(1, result.getTotalElements());
    }
    @Test
    void buscarPorApellido() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findBySurnameContainingIgnoreCase("Stark", pageable)).thenReturn(new PageImpl<>(List.of(new Employee())));
        Page<Employee> result =service.search(null, "Stark", pageable);
        assertEquals(1, result.getTotalElements());
    }
    @Test
    void buscarSinFiltros() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(new Employee())));
        Page<Employee> result = service.search(null, null, pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void eliminarEmpleados_ok() {
        Employee e1 = new Employee();
        e1.setId(1);
        Employee e2 = new Employee();
        e2.setId(2);
        when(repository.findByIdIn(List.of(1, 2, 3))).thenReturn(List.of(e1, e2));
        when(repository.deleteEmployeesByIdIn(List.of(1, 2))).thenReturn(2);
        EmployeeDTO.DeleteResponse response = service.deleteById(List.of(1, 2, 3));
        assertEquals(List.of(1, 2), response.getDeleted());
        assertEquals(List.of(3), response.getNotDeleted());    }

}