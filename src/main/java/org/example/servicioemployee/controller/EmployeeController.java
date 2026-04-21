package org.example.servicioemployee.controller;

import jakarta.validation.Valid;
import org.example.servicioemployee.dto.EmployeeBatchRequest;
import org.example.servicioemployee.service.EmployeeService;
import org.example.servicioemployee.dto.EmployeeDTO;
import org.example.servicioemployee.dto.EmployeeDTO.CreateRequest;
import org.example.servicioemployee.dto.UpdateRequest;
import org.example.servicioemployee.dto.UpdateList;
import org.example.servicioemployee.dto.EmployeeDTO.DeleteResponse;
import org.example.servicioemployee.mapper.EmployeeMapper;
import org.example.servicioemployee.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private static final String ERROR_MSG =
            "Parámetros mal informados. Los valores mínimos aceptados son size=1 y page=0. No introduzca nada para obtener los valores por defecto";

    private EmployeeService service;
    private final EmployeeMapper mapper;

    @Autowired
    public EmployeeController(EmployeeService service, EmployeeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping()
    public ResponseEntity<?> listAll(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        if (page < 0 || size < 1) {
            return ResponseEntity.badRequest().body(
                    /*crea un mapa inmutable*/
                    Map.of("message", ERROR_MSG)
            );
        }

        /**protege la logica interna ante errores*/
        try {
            Pageable pageable = PageRequest.of(page, size);
            /**Llama a la clase de servicio para obtener los datos paginados*/
            Page<Employee> employeesPage = service.getAll(pageable);
            /** Construir respuesta personalizada (con DTO)
             * El DTO ListResponse agrega metadatos de paginacion
             * para que el cliente sepa cuantas paginas y elementos hay*/
            EmployeeDTO.ListResponse response = new EmployeeDTO.ListResponse();
            response.setEmployees(mapper.toResponseList(employeesPage.getContent()));
            response.setTotalPages(employeesPage.getTotalPages());
            response.setTotalElements(employeesPage.getTotalElements());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Se ha producido un error técnico, pruebe de nuevo"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<EmployeeDTO.ListResponse> search(
            @RequestParam (value = "id",required = false)Integer id,
            @RequestParam (value = "name",required = false)String name,
            @RequestParam (value = "surname",required = false)String surname,
            Pageable pageable){
        if (id != null && (name != null || surname != null)) {
            return ResponseEntity.badRequest().build();
        }
        Page<Employee> employees;

        if (id != null) {
            Employee emp = service.findById(id);
            List<Employee> list = emp != null ? List.of(emp) : List.of();
            employees = new PageImpl<>(list, pageable, list.size());
        } else {
            employees = service.search(name, surname, pageable);
        }
        EmployeeDTO.ListResponse response = new EmployeeDTO.ListResponse();
        response.setEmployees(mapper.toResponseList(employees.getContent()));
        response.setTotalPages(employees.getTotalPages());
        response.setTotalElements(employees.getTotalElements());

        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<?>create(@RequestBody EmployeeBatchRequest request){
        if (request.getEmployees() == null || request.getEmployees().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Employee list is empty or contains invalid data");
        }

        List<String> failedNames = new ArrayList<>();
        List<Employee> inserted = new ArrayList<>();
        for (CreateRequest req : request.getEmployees()) {
            try {
                validate(req);
                Employee emp = service.save(req);
                inserted.add(emp);
            } catch (Exception e) {
                failedNames.add(req.getName() + " " + req.getSurname());
            }
        }
         if (failedNames.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseList(inserted));
        }

        String names = String.join(", ", failedNames);
        Map<String, String> warning = Map.of(
                "warning",
                "Not all employees where processed. Please check requested: " + names
        );
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(warning);
    }

    @PutMapping
    public ResponseEntity<?> updateEmployees(@RequestBody UpdateList request) {
        List<Integer> failed = new ArrayList<>();
        List<Employee> updated = new ArrayList<>();
        for (UpdateRequest actu : request.getEmployees()) {
            try {
                validateUpdate(actu);
                Employee emp = service.updateEmployee(actu);
                updated.add(emp);
            } catch (Exception e) {
                failed.add(actu.getId());
            }
        }
        if (failed.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Empleados actualizados correctamente"
            ));
        }
        String ids = failed.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.PARTIAL_CONTENT)
                .body(Map.of("warning",
                        "Not all employees where processed. Please check requested ids: " + ids
                ));
    }

    @DeleteMapping("/id/{ids}")
    public ResponseEntity<?> eliminarEmployee(@PathVariable String ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("La lista no pued ir vacia");
        }
        List<String> normalizacion = Arrays.stream(ids.split(",")).toList();
        List<Integer> validos;
        try {
            validos = normalizacion.stream().map(Integer::parseInt).toList();
        }catch(NumberFormatException e){
            return ResponseEntity.badRequest().body("Los ids de empleado deben ser valores numericos y separados por comas");
        }
        DeleteResponse eliminado = service.deleteById(validos);
        return ResponseEntity.ok("Deleted: " + eliminado.getDeleted() +
                "\nNotDeleted: " + eliminado.getNotDeleted());
    }
    private void validate(CreateRequest req) {
        if (req.getName() == null || req.getName().isBlank()) {
            throw new RuntimeException("Invalid name");
        }
        if (req.getSurname() == null || req.getSurname().isBlank()) {
            throw new RuntimeException("Invalid surname");
        }
        if (req.getMailPlexus() == null || !req.getMailPlexus().contains("@")) {
            throw new RuntimeException("Invalid plexusMail");
        }
    }

    private void validateUpdate(UpdateRequest req) {
        if (req.getId() == null) {
            throw new RuntimeException("Missing id");
        }
        if (req.getClientId() != null && req.getClientId().isBlank()) {
            throw new RuntimeException("Invalid clientId");
        }
        if (req.getMailClient() != null &&
                !req.getMailClient().contains("@")) {
            throw new RuntimeException("Invalid mailClient");
        }
        if (req.getPhoneNumber() != null &&
                !req.getPhoneNumber().matches("^[0-9]{9}$")) {
            throw new RuntimeException("Invalid phoneNumber");
        }

    }

}