package com.github.silviacristinaa.employees.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.silviacristinaa.employees.dtos.requests.EmployeeRequestDto;
import com.github.silviacristinaa.employees.dtos.requests.EmployeeStatusRequestDto;
import com.github.silviacristinaa.employees.dtos.responses.EmployeeResponseDto;
import com.github.silviacristinaa.employees.entities.Employee;
import com.github.silviacristinaa.employees.exceptions.ConflictException;
import com.github.silviacristinaa.employees.exceptions.NotFoundException;
import com.github.silviacristinaa.employees.repositories.EmployeeRepository;
import com.github.silviacristinaa.employees.services.EmployeeService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService{
	
	private static final String CPF_ALREADY_REGISTERED_IN_THE_SYSTEM = "Cpf already registered in the system";
	private static final String EMPLOYEE_NOT_FOUND = "Employee %s not found";
	
	private final EmployeeRepository employeeRepository; 
	private final ModelMapper modelMapper; 
	
	@Override
	public List<EmployeeResponseDto> findAll() {
		return employeeRepository.findAll().stream().map(employee -> modelMapper.map(employee, EmployeeResponseDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public EmployeeResponseDto findOneEmployeeById(Long id) throws NotFoundException {
		Employee employee = findById(id); 
		return modelMapper.map(employee, EmployeeResponseDto.class);
	}

	@Override
	@Transactional
	public Employee create(EmployeeRequestDto employeeRequestDto) throws ConflictException {
		findByCpf(employeeRequestDto);
		return employeeRepository.save(modelMapper.map(employeeRequestDto, Employee.class));
	}
	
	@Override
	@Transactional
	public void updateEmployeeStatus(Long id, EmployeeStatusRequestDto employeeStatusRequestDto) throws NotFoundException {
		Employee employee = findById(id);
		
		employee.setEnabled(employeeStatusRequestDto.isEnabled());
		employee.setId(id);
		employeeRepository.save(employee); 
	}

	@Override
	@Transactional
	public void update(Long id, EmployeeRequestDto employeeRequestDto) throws NotFoundException, ConflictException {
		findById(id);
		findByCpf(employeeRequestDto, id);
		
		Employee employee = modelMapper.map(employeeRequestDto, Employee.class);
		employee.setId(id);
		
		employeeRepository.save(employee);
	}

	@Override
	@Transactional
	public void delete(Long id) throws NotFoundException {
		findById(id);
		employeeRepository.deleteById(id);
	}
	
	private Employee findById(Long id) throws NotFoundException {
		return employeeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException(String.format(EMPLOYEE_NOT_FOUND, id)));				
	}
	
	private void findByCpf(EmployeeRequestDto employeeRequestDto) throws ConflictException {
		Optional<Employee> employee = employeeRepository.findByCpf(employeeRequestDto.getCpf());
		if(employee.isPresent()) {
			throw new ConflictException(CPF_ALREADY_REGISTERED_IN_THE_SYSTEM);
		}
	}
	
	private void findByCpf(EmployeeRequestDto employeeRequestDto, Long id) throws ConflictException {
		Optional<Employee> employee = employeeRepository.findByCpf(employeeRequestDto.getCpf());
		if(employee.isPresent() && !id.equals(employee.get().getId())) {
			throw new ConflictException(CPF_ALREADY_REGISTERED_IN_THE_SYSTEM);
		}
	}
}	