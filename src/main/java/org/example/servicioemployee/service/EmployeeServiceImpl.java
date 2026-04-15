package org.example.servicioemployee.service;


import org.example.servicioemployee.dto.EmployeeDTO;
import org.example.servicioemployee.dto.UpdateRequest;
import org.example.servicioemployee.dto.EmployeeDTO.CreateRequest;
import org.example.servicioemployee.dto.EmployeeDTO.DeleteResponse;
import org.example.servicioemployee.dto.UpdateList;

import org.example.servicioemployee.model.Employee;
import org.example.servicioemployee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    public EmployeeServiceImpl(EmployeeRepository repository) {
        this.repository = repository;
    }

    /**este metodo nos da todos los empleados compaginados*/
    @Override
    public Page<Employee> getAll(Pageable pageable){
        return repository.findAll(pageable);
    }

    /**este metodo busca empleado por id y si no existe lanza un error*/
    @Override
    public Employee findById(Integer id){
        return repository.findById(id).orElseThrow(()->new RuntimeException("No encontrado"));
    }

    /**este metodo guarda o actualiza un empleado en la base de datos*/
    @Override
    public Employee save(CreateRequest request){
        Employee employee=new Employee();
        employee.setName(request.getName());
        employee.setSurname(request.getSurname());
        employee.setMailPlexus(request.getMailPlexus());
        employee.setClientId(request.getClientId());
        employee.setMailClient(request.getMailClient());
        employee.setPhoneNumber(request.getPhoneNumber());
        return repository.save(employee);
    }

    @Override
    public List<Employee> saveAll(List<CreateRequest>request){
        return request.stream().map(this::save).toList();
    }

    @Override
    public Employee updateEmployee(UpdateRequest request) {
        Employee emp = repository.findById(request.getId()).orElseThrow(() -> new RuntimeException("Empleado no encontrado: " + request.getId()));
        // SOLO estos 3 campos están permitidos
        if (request.getClientId() != null)
            emp.setClientId(request.getClientId());
        if (request.getMailClient() != null)
            emp.setMailClient(request.getMailClient());
        if (request.getPhoneNumber() != null)
            emp.setPhoneNumber(request.getPhoneNumber());
        return repository.save(emp);

    }

    @Override
    public Page<Employee> search(String name, String surname, Pageable pageable) {
        //si el nombre no es nulo ni esta en blanco y a la vez el surname no es nulo ni en blanco hace la busqueda por nombre y apellido
        if (name != null && !name.isBlank() && surname != null && !surname.isBlank()) {
            return repository.findByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(name, surname, pageable);
        }
        //si el nombre no es nulo ni en blanco hace la busqueda por nombre
        if (name != null && !name.isBlank()) {
            return repository.findByNameContainingIgnoreCase(name, pageable);
        }
        //si el surname no es nulo ni esta en blanco hace la busqueda por surname
        if (surname != null && !surname.isBlank()) {
            return repository.findBySurnameContainingIgnoreCase(surname, pageable);
        }
        // Si no se da ningún filtro → lista completa (controlado por el controller)
        return repository.findAll(pageable);
    }

    /**
     * este metodo elimina un empleado por  su id
     *
     * @return
     */
    @Override
    public DeleteResponse deleteById(List<Integer> employeesID){
        //busca los existentes en la base de datos
        List<Employee> existentes =repository.findByIdIn(employeesID);
        //los id existentes lo convertimos a lista
        List<Integer> deleted= existentes.stream()
                .map(Employee::getId).toList();
        //guardamos los id que no existen
        List<Integer> notDeleted=employeesID.stream()
                .filter(id->!deleted.contains(id))
                .toList();

        int deletedCount=0;
        if(!deleted.isEmpty()){
            deletedCount=repository.deleteEmployeesByIdIn(deleted);
        }
        return new DeleteResponse(deleted,notDeleted);
    }
}
