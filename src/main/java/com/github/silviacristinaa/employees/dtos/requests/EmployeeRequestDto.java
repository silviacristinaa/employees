package com.github.silviacristinaa.employees.dtos.requests;

import javax.validation.constraints.NotBlank;

import com.github.silviacristinaa.employees.enums.DepartmentEnum;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmployeeRequestDto {
	
	@NotBlank
	private String name;
	@NotBlank
	private String cpf; 
	private DepartmentEnum department; 
	private boolean enabled;
}