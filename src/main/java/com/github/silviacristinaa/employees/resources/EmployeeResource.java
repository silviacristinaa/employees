package com.github.silviacristinaa.employees.resources;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.silviacristinaa.employees.dtos.requests.EmployeeRequestDto;
import com.github.silviacristinaa.employees.dtos.requests.EmployeeStatusRequestDto;
import com.github.silviacristinaa.employees.dtos.responses.EmployeeResponseDto;
import com.github.silviacristinaa.employees.exceptions.ConflictException;
import com.github.silviacristinaa.employees.exceptions.NotFoundException;
import com.github.silviacristinaa.employees.services.EmployeeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/employees")
@RequiredArgsConstructor
@Api(value = "Funcionários", tags = {"Serviço para Controle de Funcionários"})
public class EmployeeResource {
	
	private static final String ID = "/{id}";
	
	private final EmployeeService employeeService; 
	
	@GetMapping
	@ApiOperation(value="Retorna todos os funcionários", httpMethod = "GET")
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<List<EmployeeResponseDto>> findAll() {
		return ResponseEntity.ok(employeeService.findAll());
	}

	@GetMapping(value = ID)
	@ApiOperation(value="Retorna um funcionário único", httpMethod = "GET")
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<EmployeeResponseDto> findById(@PathVariable Long id) throws NotFoundException {
		return ResponseEntity.ok(employeeService.findOneEmployeeById(id));
	}

	@PostMapping
	@ApiOperation(value="Cria um funcionário", httpMethod = "POST")
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<Void> create(@RequestBody @Valid EmployeeRequestDto employeeRequestDto) throws ConflictException {
		URI uri = ServletUriComponentsBuilder
				.fromCurrentRequest().path(ID).buildAndExpand(employeeService.create(employeeRequestDto).getId()).toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@PatchMapping(value = ID)
	@ApiOperation(value="Atualiza o status de um funcionário", httpMethod = "PATCH")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> updateEmployeeStatus(@PathVariable Long id, 
			@RequestBody EmployeeStatusRequestDto employeeStatusRequestDto) throws NotFoundException {
		employeeService.updateEmployeeStatus(id, employeeStatusRequestDto);
		return ResponseEntity.noContent().build();
	}

	@PutMapping(value = ID)
	@ApiOperation(value="Atualiza um funcionário", httpMethod = "PUT")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid EmployeeRequestDto employeeRequestDto) throws NotFoundException, ConflictException {
		employeeService.update(id, employeeRequestDto);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(value = ID)
	@ApiOperation(value="Deleta um funcionário", httpMethod = "DELETE")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> delete(@PathVariable Long id) throws NotFoundException {
		employeeService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
