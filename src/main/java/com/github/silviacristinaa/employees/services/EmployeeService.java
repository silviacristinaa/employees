package com.github.silviacristinaa.employees.services;

import java.util.List;

import com.github.silviacristinaa.employees.dtos.requests.EmployeeRequestDto;
import com.github.silviacristinaa.employees.dtos.requests.EmployeeStatusRequestDto;
import com.github.silviacristinaa.employees.dtos.responses.EmployeeResponseDto;
import com.github.silviacristinaa.employees.entities.Employee;
import com.github.silviacristinaa.employees.exceptions.ConflictException;
import com.github.silviacristinaa.employees.exceptions.NotFoundException;

public interface EmployeeService {

	List<EmployeeResponseDto> findAll();
	
	EmployeeResponseDto findOneEmployeeById(Long id) throws NotFoundException;
	
	Employee create(EmployeeRequestDto employeeRequestDto) throws ConflictException; 
	
	void updateEmployeeStatus(Long id, EmployeeStatusRequestDto employeeStatusRequestDto) throws NotFoundException; 
	
	void update(Long id, EmployeeRequestDto employeeRequestDto) throws NotFoundException, ConflictException; 
	
	void delete(Long id) throws NotFoundException;
}
