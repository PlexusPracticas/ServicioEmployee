package org.example.servicioemployee.repository;


import jakarta.transaction.Transactional;
import org.example.servicioemployee.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**nos permite usar la interfaz en el services*/
@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Integer> {//el extends crea un repositorio que nos permitira usar funciones sin escribir nada

    Page<Employee> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Employee> findBySurnameContainingIgnoreCase(String surname, Pageable pageable);
    Page<Employee> findByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(
            String name,
            String surname,
            Pageable pageable
    );
    List<Employee> findByIdIn(List<Integer> employeeId);
    @Modifying
    @Transactional
    @Query("DELETE FROM Employee e WHERE e.id IN :employeesId")
    int deleteEmployeesByIdIn(@Param("employeesId") List<Integer> employeesId);

}
