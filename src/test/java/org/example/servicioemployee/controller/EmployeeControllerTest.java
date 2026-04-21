package org.example.servicioemployee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.servicioemployee.dto.EmployeeBatchRequest;
import org.example.servicioemployee.dto.EmployeeDTO;
import org.example.servicioemployee.dto.UpdateList;
import org.example.servicioemployee.dto.UpdateRequest;
import org.example.servicioemployee.mapper.EmployeeMapper;
import org.example.servicioemployee.model.Employee;
import org.example.servicioemployee.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService service;

    @MockBean
    private EmployeeMapper mapper;

    @Test
    void listarEmpleados_ok() throws Exception {
        Mockito.when(service.getAll(Mockito.any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employees").exists());
    }

    @Test
    void listAll_badParams() throws Exception {
        mockMvc.perform(get("/employees")
                        .param("page", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void search_IdAndName_returns400() throws Exception {
        mockMvc.perform(get("/employees/search")
                .param("id", "1")
                .param("name", "Tony"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void search_byId_ok() throws Exception {

        Employee employee = new Employee();
        employee.setId(1);

        Mockito.when(service.findById(1))
                .thenReturn(employee);

        Mockito.when(mapper.toResponseList(Mockito.any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/employees/search")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.employees").exists());
    }

    @Test
    void search_byNameOrSurname_ok() throws Exception {
        Mockito.when(service.search(
                Mockito.eq("Tony"),
                Mockito.eq("Stark"),
                Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new Employee())));

        Mockito.when(mapper.toResponseList(Mockito.any())).thenReturn(List.of());

        mockMvc.perform(get("/employees/search")
                .param("name", "Tony")
                .param("surname", "Stark"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employees").exists())
                .andExpect(jsonPath("$.totalElements").value(1));
    }


    @Test
    void crearEmpleados_okreturns201() throws Exception {

        EmployeeDTO.CreateRequest req = new EmployeeDTO.CreateRequest();
        req.setName("Tony");
        req.setSurname("Stark");
        req.setMailPlexus("tony@plexus.com");

        EmployeeBatchRequest batch = new EmployeeBatchRequest();
        batch.setEmployees(List.of(req));

        Mockito.when(service.save(Mockito.any())).thenReturn(new Employee());

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batch)))
                .andExpect(status().isCreated());
    }

    @Test
    void crearEmpleados_parcialreturns206() throws Exception {

        EmployeeDTO.CreateRequest ok = new EmployeeDTO.CreateRequest();
        ok.setName("Tony");
        ok.setSurname("Stark");
        ok.setMailPlexus("tony@plexus.com");

        EmployeeDTO.CreateRequest bad = new EmployeeDTO.CreateRequest();
        bad.setName("Bruce");
        bad.setSurname("Banner");
        bad.setMailPlexus("invalidMail");

        EmployeeBatchRequest batch = new EmployeeBatchRequest();
        batch.setEmployees(List.of(ok, bad));

        Mockito.when(service.save(Mockito.any())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batch)))
                .andExpect(status().isPartialContent())
                .andExpect(jsonPath("$.warning").exists());
    }

    @Test
    void actualizarEmpleados_ok_returns201() throws Exception {

        UpdateRequest r1 = new UpdateRequest();
        r1.setId(1);
        r1.setPhoneNumber("699999999");

        UpdateRequest r2 = new UpdateRequest();
        r2.setId(2);
        r2.setMailClient("test@cliente.es");

        UpdateList request = new UpdateList();
        request.setEmployees(List.of(r1, r2));

        Mockito.when(service.updateEmployee(Mockito.any())).thenReturn(new Employee());

        mockMvc.perform(put("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("Empleados actualizados correctamente"));
    }

    @Test
    void actualizarEmpleados_parcial_returns206() throws Exception {

        UpdateRequest ok = new UpdateRequest();
        ok.setId(1);
        ok.setPhoneNumber("699999999");

        UpdateRequest fail = new UpdateRequest();
        fail.setId(4);
        fail.setPhoneNumber("699999999");

        UpdateList request = new UpdateList();
        request.setEmployees(List.of(ok, fail));

        Mockito.when(service.updateEmployee(Mockito.argThat(r -> r != null && r.getId() == 1))).thenReturn(new Employee());

        Mockito.when(service.updateEmployee(Mockito.argThat(r -> r != null && r.getId() == 4))).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(put("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isPartialContent())
                .andExpect(jsonPath("$.warning")
                        .value("Not all employees where processed. Please check requested ids: 4"));
    }


    @Test
    void deleteEmployees_ok() throws Exception {

        EmployeeDTO.DeleteResponse response = new EmployeeDTO.DeleteResponse(List.of(1, 2),List.of(3));
        Mockito.when(service.deleteById(Mockito.any())).thenReturn(response);
        mockMvc.perform(delete("/employees/id/1,2,3"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Deleted: [1, 2]\nNotDeleted: [3]"));
    }

    @Test
    void eliminarEmpleados_idsInvalidos_returns400() throws Exception {
        mockMvc.perform(delete("/employees/id/a,b,c"))
                .andExpect(status().isBadRequest());
    }


}