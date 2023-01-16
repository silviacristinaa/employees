package com.github.silviacristinaa.employees.resources.employeeIntegration;

import com.github.silviacristinaa.employees.entities.Employee;
import com.github.silviacristinaa.employees.enums.DepartmentEnum;
import com.github.silviacristinaa.employees.repositories.EmployeeRepository;
import com.github.silviacristinaa.employees.resources.integrations.IntegrationTests;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EmployeeResourceIntegrationTest extends IntegrationTests {

    private String employeeId;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @Order(1)
    public void whenTryCreateEmployeeWithInvalidFieldsReturnBadRequestException() throws Exception {
        mvc.perform(post("/employees").headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                EmployeeResourceIntegrationBody.employeeException())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Arguments not valid")));
    }

    @Test
    @Order(2)
    public void whenCreateEmployeeReturnCreated() throws Exception {
        mvc.perform(post("/employees").headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                EmployeeResourceIntegrationBody.employeeSuccess())))
                .andExpect(status().isCreated())
                .andDo(i -> {
                    employeeId = getIdByLocation(i.getResponse().getHeader("Location"));
                });
    }

    @Test
    @Order(3)
    public void whenTryCreateEmployeeWithAlreadyExistingCpfReturnConflictException() throws Exception {
        mvc.perform(post("/employees").headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                EmployeeResourceIntegrationBody.employeeSuccess())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message", is("Conflict")))
                .andExpect(jsonPath("errors.[0]", is("Cpf already registered in the system")));
    }

    @Test
    @Order(4)
    public void whenFindAllReturnSuccess() throws Exception {
        mvc.perform(get("/employees").headers(mockHttpHeaders()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].name", is("Test")))
                .andExpect(jsonPath("content[0].cpf", is("88888888888")))
                .andExpect(jsonPath("content[0].department", is("IT")))
                .andExpect(jsonPath("content[0].enabled", is(true)));
    }

    @Test
    @Order(5)
    public void whenFindByFiltersReturnOk() throws Exception {
        mvc.perform(get("/employees/filters")
                        .param("department", "IT")
                        .param("enabled", "true")
                        .param("size", "1")
                        .headers(mockHttpHeaders())).andExpect(status().isOk())
                .andExpect(jsonPath("totalActive").exists())
                .andExpect(jsonPath("totalInactive").exists())
                .andExpect(jsonPath("employeeResponseDataDto.totalElements").value(1))
                .andExpect(jsonPath("employeeResponseDataDto.content").isArray())
                .andExpect(jsonPath("employeeResponseDataDto.content").isNotEmpty())
                .andExpect(jsonPath("employeeResponseDataDto.content[0].id").exists())
                .andExpect(jsonPath("employeeResponseDataDto.content[0].name").exists())
                .andExpect(jsonPath("employeeResponseDataDto.content[0].cpf").exists())
                .andExpect(jsonPath("employeeResponseDataDto.content[0].department").value("IT"))
                .andExpect(jsonPath("employeeResponseDataDto.content[0].enabled").value(true));
    }

    @Test
    @Order(6)
    public void whenFindByFilterWithoutFiltersReturnOk() throws Exception {
        mvc.perform(get("/employees/filters")
                        .headers(mockHttpHeaders())).andExpect(status().isOk())
                .andExpect(jsonPath("totalActive").exists())
                .andExpect(jsonPath("totalInactive").exists())
                .andExpect(jsonPath("employeeResponseDataDto.content").isArray())
                .andExpect(jsonPath("employeeResponseDataDto.content").isNotEmpty());
    }

    @Test
    @Order(7)
    public void whenTryFindByIdWithIncorrectIdReturnNotFound() throws Exception {
        mvc.perform(get("/employees/{id}", 999).headers(mockHttpHeaders()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is("Not found")))
                .andExpect(jsonPath("errors.[0]", is("Employee 999 not found")));
    }

    @Test
    @Order(8)
    public void whenFindByIdWithCorrectIdReturnSuccess() throws Exception {
        mvc.perform(get("/employees/{id}", employeeId).headers(mockHttpHeaders()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Test")))
                .andExpect(jsonPath("cpf", is("88888888888")))
                .andExpect(jsonPath("department", is("IT")))
                .andExpect(jsonPath("enabled", is(true)));
    }

    @Test
    @Order(9)
    public void whenTryUpdateEmployeeStatusWithIncorrectIdReturnNotFound() throws Exception {
        mvc.perform(patch("/employees/{id}", 999).headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                EmployeeResourceIntegrationBody.updateEmployeeStatus())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is("Not found")))
                .andExpect(jsonPath("errors.[0]", is("Employee 999 not found")));
    }

    @Test
    @Order(10)
    public void whenUpdateEmployeeStatusWithCorrectIdReturnNoContent() throws Exception {
        mvc.perform(patch("/employees/{id}", employeeId).headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                EmployeeResourceIntegrationBody.updateEmployeeStatus())))
                .andExpect(status().isNoContent());

        Optional<Employee> employee = employeeRepository.findById(Long.valueOf(employeeId));
        assertTrue(employee.isPresent());
        assertEquals(employee.get().getName(), "Test");
        assertEquals(employee.get().getCpf(), "88888888888");
        assertEquals(employee.get().getDepartment(), DepartmentEnum.IT);
        assertEquals(employee.get().isEnabled(), false);
    }

    @Test
    @Order(11)
    public void whenTryUpdateEmployeeWithInvalidFieldsReturnBadRequestException() throws Exception {
        mvc.perform(put("/employees/{id}", employeeId).headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                EmployeeResourceIntegrationBody.employeeException())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Arguments not valid"));
    }

    @Test
    @Order(12)
    public void whenTryUpdateEmployeeWithIncorrectIdReturnNotFound() throws Exception {
        mvc.perform(put("/employees/{id}", 999).headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                EmployeeResourceIntegrationBody.employeeSuccess())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is("Not found")))
                .andExpect(jsonPath("errors.[0]", is("Employee 999 not found")));
    }

    @Test
    @Order(13)
    public void whenTryUpdateEmployeeWithAlreadyExistingCpfReturnConflictException() throws Exception {
        Employee employee = new Employee(null, "Test", "00000000000",DepartmentEnum.IT, true);
        employee = employeeRepository.save(employee);

        mvc.perform(put("/employees/{id}", employee.getId()).headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                EmployeeResourceIntegrationBody.employeeSuccess())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message", is("Conflict")))
                .andExpect(jsonPath("errors.[0]", is("Cpf already registered in the system")));
    }

    @Test
    @Order(14)
    public void whenUpdateEmployeeWithCorrectIdReturnNoContent() throws Exception {
        mvc.perform(put("/employees/{id}", employeeId).headers(mockHttpHeaders())
                        .content(objectMapper.writeValueAsString(
                                EmployeeResourceIntegrationBody.employeeSuccess())))
                .andExpect(status().isNoContent());

        Optional<Employee> employee = employeeRepository.findById(Long.valueOf(employeeId));
        assertTrue(employee.isPresent());
        assertEquals(employee.get().getName(), "Test");
        assertEquals(employee.get().getCpf(), "88888888888");
        assertEquals(employee.get().getDepartment(), DepartmentEnum.IT);
        assertEquals(employee.get().isEnabled(), true);
    }

    @Test
    @Order(15)
    public void whenTryDeleteEmployeeWithIncorrectIdReturnNotFound() throws Exception {
        mvc.perform(delete("/employees/{id}", 999).headers(mockHttpHeaders()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is("Not found")))
                .andExpect(jsonPath("errors.[0]", is("Employee 999 not found")));
    }

    @Test
    @Order(16)
    public void whenDeleteEmployeeWithCorrectIdReturnNoContent() throws Exception {
        mvc.perform(delete("/employees/{id}", employeeId).headers(mockHttpHeaders()))
                .andExpect(status().isNoContent());

        Optional<Employee> employee = employeeRepository.findById(Long.valueOf(employeeId));
        assertFalse(employee.isPresent());
    }
}