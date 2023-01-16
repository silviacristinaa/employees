package com.github.silviacristinaa.employees.resources.employeeIntegration;

import com.github.silviacristinaa.employees.dtos.requests.EmployeeRequestDto;
import com.github.silviacristinaa.employees.dtos.requests.EmployeeStatusRequestDto;
import com.github.silviacristinaa.employees.enums.DepartmentEnum;

public class EmployeeResourceIntegrationBody {
    public static EmployeeRequestDto employeeException() {
        return new EmployeeRequestDto(null, "88888888888", DepartmentEnum.IT, true);
    }

    public static EmployeeRequestDto employeeSuccess() {
        return new EmployeeRequestDto("Test", "88888888888", DepartmentEnum.IT, true);
    }

    public static EmployeeStatusRequestDto updateEmployeeStatus() {
        return new EmployeeStatusRequestDto(false);
    }
}