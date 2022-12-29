package com.github.silviacristinaa.employees.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.github.silviacristinaa.employees.dtos.requests.EmployeeRequestDto;
import com.github.silviacristinaa.employees.dtos.requests.EmployeeStatusRequestDto;
import com.github.silviacristinaa.employees.dtos.responses.EmployeeResponseDataDto;
import com.github.silviacristinaa.employees.dtos.responses.EmployeeResponseDto;
import com.github.silviacristinaa.employees.entities.Employee;
import com.github.silviacristinaa.employees.enums.DepartmentEnum;
import com.github.silviacristinaa.employees.exceptions.ConflictException;
import com.github.silviacristinaa.employees.exceptions.NotFoundException;
import com.github.silviacristinaa.employees.services.EmployeeService;

@ExtendWith(SpringExtension.class)
public class EmployeeResourceTest {
	
	private static final long ID = 1l;
	private static final String NAME = "Test";
	private static final String CPF = "test";
	private static final int INDEX = 0;
	
	private EmployeeRequestDto employeeRequestDto;
	private EmployeeStatusRequestDto employeeStatusRequestDto;
	private EmployeeResponseDataDto employeeResponseDataDto;
	private EmployeeResponseDto employeeResponseDto; 
	
	private Employee employee;

	@InjectMocks
	private EmployeeResource employeeResource;

	@Mock
	private EmployeeService employeeService;
	
	@BeforeEach
	void setUp() {
		employeeRequestDto = new EmployeeRequestDto(NAME, CPF, DepartmentEnum.IT, true);
		
		employeeStatusRequestDto = new EmployeeStatusRequestDto(false);
		
		employeeResponseDataDto = new EmployeeResponseDataDto(ID, NAME, CPF, DepartmentEnum.IT, true);
		
		List<EmployeeResponseDataDto> responseDataDto = new ArrayList<>(); 
		responseDataDto.add(employeeResponseDataDto);
		
		Page<EmployeeResponseDataDto> page = new PageImpl<>(responseDataDto);
		
		employeeResponseDto = new EmployeeResponseDto(1l, 0l, page);
		
		employee = new Employee();
	}
	
	@Test
	void whenFindAllReturnEmployeeResponseDataDtoPage() {
		when(employeeService.findAll(Mockito.any(Pageable.class)))
				.thenReturn(new PageImpl<>(Arrays.asList(employeeResponseDataDto)));

		ResponseEntity<Page<EmployeeResponseDataDto>> response = employeeResource.findAll(Pageable.unpaged());

		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(ResponseEntity.class, response.getClass());
		assertEquals(EmployeeResponseDataDto.class, response.getBody().getContent().get(INDEX).getClass()); 

		assertEquals(ID, response.getBody().getContent().get(INDEX).getId());
		assertEquals(NAME, response.getBody().getContent().get(INDEX).getName()); 
		assertEquals(CPF, response.getBody().getContent().get(INDEX).getCpf());
		assertEquals(DepartmentEnum.IT, response.getBody().getContent().get(INDEX).getDepartment());
		assertEquals(true, response.getBody().getContent().get(INDEX).isEnabled());
	}
	
	@Test
	void whenFindByFiltersReturnOneEmployeeResponseDto() {
		when(employeeService.findByFilters(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(employeeResponseDto);

		ResponseEntity<EmployeeResponseDto> response = employeeResource.findByFilters(null, null, Pageable.unpaged());

		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(ResponseEntity.class, response.getClass());
		assertEquals(EmployeeResponseDto.class, response.getBody().getClass());
		
		assertEquals(1L, response.getBody().getTotalActive());
		assertEquals(0l, response.getBody().getTotalInactive());
		
		assertEquals(ID, response.getBody().getEmployeeResponseDataDto().getContent().get(INDEX).getId());
		assertEquals(NAME, response.getBody().getEmployeeResponseDataDto().getContent().get(INDEX).getName());
		assertEquals(CPF, response.getBody().getEmployeeResponseDataDto().getContent().get(INDEX).getCpf());
		assertEquals(DepartmentEnum.IT, response.getBody().getEmployeeResponseDataDto().getContent().get(INDEX).getDepartment());
		assertEquals(true, response.getBody().getEmployeeResponseDataDto().getContent().get(INDEX).isEnabled());
	}
	
	@Test
	void whenFindByIdReturnOneEmployeeResponseDataDto() throws NotFoundException {
		when(employeeService.findOneEmployeeById(anyLong())).thenReturn(employeeResponseDataDto);
		
		ResponseEntity<EmployeeResponseDataDto> response = employeeResource.findById(ID);
		
		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(ResponseEntity.class, response.getClass());
		assertEquals(EmployeeResponseDataDto.class, response.getBody().getClass());
		
		assertEquals(ID, response.getBody().getId());
		assertEquals(NAME, response.getBody().getName());
		assertEquals(CPF, response.getBody().getCpf());
		assertEquals(DepartmentEnum.IT, response.getBody().getDepartment());
		assertEquals(true, response.getBody().isEnabled());
	}
	
	@Test
	void whenCreateEmployeeReturnCreated() throws ConflictException {
		when(employeeService.create(Mockito.any())).thenReturn(employee);

		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		ResponseEntity<Void> response = employeeResource.create(employeeRequestDto);

		assertEquals(ResponseEntity.class, response.getClass());
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getHeaders().get("Location"));
	}
	
	@Test
	void whenUpdateEmployeeStatusReturnNoContent() throws NotFoundException {
		ResponseEntity<Void> response = employeeResource.updateEmployeeStatus(ID, employeeStatusRequestDto);
		
		assertNotNull(response);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); 
		assertEquals(ResponseEntity.class, response.getClass());
	}
	
	@Test
	void whenUpdateReturnNoContent() throws NotFoundException, ConflictException {
		ResponseEntity<Void> response = employeeResource.update(ID, employeeRequestDto);
		
		assertNotNull(response);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); 
		assertEquals(ResponseEntity.class, response.getClass());
	}
	
	@Test
	void whenDeleteReturnNoContent() throws NotFoundException {
		ResponseEntity<Void> response = employeeResource.delete(ID);
		
		assertNotNull(response);
		assertEquals(ResponseEntity.class, response.getClass());
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(employeeService, times(1)).delete(anyLong());
	}
}