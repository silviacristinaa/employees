package com.github.silviacristinaa.employees.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.silviacristinaa.employees.dtos.requests.EmployeeRequestDto;
import com.github.silviacristinaa.employees.dtos.requests.EmployeeStatusRequestDto;
import com.github.silviacristinaa.employees.dtos.responses.EmployeeResponseDataDto;
import com.github.silviacristinaa.employees.dtos.responses.EmployeeResponseDto;
import com.github.silviacristinaa.employees.entities.Employee;
import com.github.silviacristinaa.employees.enums.DepartmentEnum;
import com.github.silviacristinaa.employees.exceptions.ConflictException;
import com.github.silviacristinaa.employees.exceptions.NotFoundException;
import com.github.silviacristinaa.employees.repositories.EmployeeRepository;

@ExtendWith(SpringExtension.class)
public class EmployeeServiceImplTest {
	
	private static final long ID = 1l;
	private static final String NAME = "Test";
	private static final String CPF = "test";
	private static final int INDEX = 0;
	
	private EmployeeRequestDto employeeRequestDto;
	private EmployeeStatusRequestDto employeeStatusRequestDto;
	private EmployeeResponseDataDto employeeResponseDataDto;
	
	private Employee employee;

	@InjectMocks
	private EmployeeServiceImpl employeeServiceImpl;

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private ModelMapper modelMapper;

	@BeforeEach
	void setUp() {
		employeeRequestDto = new EmployeeRequestDto(NAME, CPF, DepartmentEnum.IT, false);

		employeeStatusRequestDto = new EmployeeStatusRequestDto(true);

		employeeResponseDataDto = new EmployeeResponseDataDto(ID, NAME, CPF, DepartmentEnum.IT, true);
		
		employee = new Employee(ID, NAME, CPF, DepartmentEnum.IT, true);
	}
	
	@Test
	void whenFindAllReturnEmployeeResponseDataDtoPage() {
		when(employeeRepository.findAll()).thenReturn(List.of(employee));
		when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(employeeResponseDataDto);

		Page<EmployeeResponseDataDto> response = employeeServiceImpl.findAll(Pageable.ofSize(1));

		assertNotNull(response);
		assertEquals(1, response.getSize());
		assertEquals(EmployeeResponseDataDto.class, response.getContent().get(INDEX).getClass());

		assertEquals(ID, response.getContent().get(INDEX).getId()); 
		assertEquals(NAME, response.getContent().get(INDEX).getName());
		assertEquals(CPF, response.getContent().get(INDEX).getCpf());
		assertEquals(DepartmentEnum.IT, response.getContent().get(INDEX).getDepartment());
		assertEquals(true, response.getContent().get(INDEX).isEnabled());
	}
	
	@Test
	void whenFindByFiltersReturnOneEmployeeResponseDto() {
		when(employeeRepository.findByDepartmentAndStatus(Mockito.any(), Mockito.any())).thenReturn(List.of(employee));
		when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(employeeResponseDataDto);
		
		EmployeeResponseDto response = employeeServiceImpl.findByFilters(null, null, Pageable.ofSize(1));
		
		assertNotNull(response);
		
		assertEquals(EmployeeResponseDto.class, response.getClass());
		assertEquals(1l, response.getTotalActive()); 
		assertEquals(0l, response.getTotalInactive()); 
		
		assertEquals(1, response.getEmployeeResponseDataDto().getSize());
		assertEquals(ID, response.getEmployeeResponseDataDto().getContent().get(INDEX).getId());
		assertEquals(NAME, response.getEmployeeResponseDataDto().getContent().get(INDEX).getName());
		assertEquals(CPF, response.getEmployeeResponseDataDto().getContent().get(INDEX).getCpf());
		assertEquals(DepartmentEnum.IT, response.getEmployeeResponseDataDto().getContent().get(INDEX).getDepartment());
		assertEquals(true, response.getEmployeeResponseDataDto().getContent().get(INDEX).isEnabled());
	}
	
	@Test
	void whenFindByIdReturnOneEmployeeResponseDataDto() throws NotFoundException {
		when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));
		when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(employeeResponseDataDto);

		EmployeeResponseDataDto response = employeeServiceImpl.findOneEmployeeById(ID);

		assertNotNull(response);

		assertEquals(EmployeeResponseDataDto.class, response.getClass());
		assertEquals(ID, response.getId());
		assertEquals(NAME, response.getName());
		assertEquals(CPF, response.getCpf());
		assertEquals(DepartmentEnum.IT, response.getDepartment());
		assertEquals(true, response.isEnabled());
	}
	
	@Test
	void whenTryFindByIdReturnNotFoundException() {
		when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

		NotFoundException exception = assertThrows(NotFoundException.class, () -> employeeServiceImpl.findOneEmployeeById(ID));

		assertEquals(String.format("Employee %s not found", ID), exception.getMessage());
	}
	
	@Test
	void whenCreateReturnSuccess() throws ConflictException {
		when(employeeRepository.findByCpf(Mockito.any())).thenReturn(Optional.empty());
		when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(employee);
		when(employeeRepository.save(Mockito.any())).thenReturn(employee);

		Employee response = employeeServiceImpl.create(employeeRequestDto);

		assertNotNull(response);
		assertEquals(Employee.class, response.getClass());
		assertEquals(ID, response.getId());
		assertEquals(NAME, response.getName());
		assertEquals(CPF, response.getCpf());
		assertEquals(DepartmentEnum.IT, response.getDepartment());
		assertEquals(true, response.isEnabled());

		verify(employeeRepository, times(1)).save(Mockito.any());
	}
	
	@Test
	void whenTryCreateReturnConflictException() {
		when(employeeRepository.findByCpf(Mockito.any())).thenReturn(Optional.of(employee));
		
		ConflictException exception = assertThrows(ConflictException.class,
				() -> employeeServiceImpl.create(employeeRequestDto));

		assertEquals("Cpf already registered in the system", exception.getMessage());
	}
	
	@Test
	void whenUpdateEmployeeStatusReturnSuccess() throws NotFoundException {
		when(employeeRepository.findById(Mockito.any())).thenReturn(Optional.of(employee));

		employeeServiceImpl.updateEmployeeStatus(ID, employeeStatusRequestDto);

		verify(employeeRepository, times(1)).save(Mockito.any());
	}
	
	@Test
	void whenTryUpdateEmployeeStatusReturnNotFoundException() {
		when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> employeeServiceImpl.updateEmployeeStatus(ID, employeeStatusRequestDto));

		assertEquals(String.format("Employee %s not found", ID), exception.getMessage());
	}

	@Test
	void whenUpdateReturnSuccess() throws NotFoundException, ConflictException {
		when(employeeRepository.findById(Mockito.any())).thenReturn(Optional.of(employee));
		when(employeeRepository.findByCpf(Mockito.any())).thenReturn(Optional.empty());
		when(modelMapper.map(Mockito.any(), Mockito.any())).thenReturn(employee);

		employeeServiceImpl.update(ID, employeeRequestDto);

		verify(employeeRepository, times(1)).save(Mockito.any());
	}

	@Test
	void whenTryUpdateReturnNotFoundException() {
		when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> employeeServiceImpl.update(ID, employeeRequestDto));

		assertEquals(String.format("Employee %s not found", ID), exception.getMessage());
	}
	
	@Test
	void whenTryUpdateReturnConflictException() {
		when(employeeRepository.findById(Mockito.any())).thenReturn(Optional.of(employee));
		when(employeeRepository.findByCpf(Mockito.any())).thenReturn(Optional.of(employee));
		
		employee.setId(2l);
		
		ConflictException exception = assertThrows(ConflictException.class,
				() -> employeeServiceImpl.update(ID, employeeRequestDto));

		assertEquals("Cpf already registered in the system", exception.getMessage());
	}
	
	@Test
	void whenDeleteReturnSuccess() throws NotFoundException {
		when(employeeRepository.findById(Mockito.any())).thenReturn(Optional.of(employee));
		
		employeeServiceImpl.delete(ID);
		
		verify(employeeRepository, times(1)).deleteById(anyLong());
	}

	@Test
	void whenTryDeleteReturnNotFoundException() {
		when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

		NotFoundException exception = assertThrows(NotFoundException.class, () -> employeeServiceImpl.delete(ID));

		assertEquals(String.format("Employee %s not found", ID), exception.getMessage());
	}
}