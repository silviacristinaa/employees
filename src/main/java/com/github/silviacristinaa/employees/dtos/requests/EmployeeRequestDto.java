package com.github.silviacristinaa.employees.dtos.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.github.silviacristinaa.employees.enums.DepartmentEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class EmployeeRequestDto {
	
	@NotBlank
	private String name;
	@NotBlank
	private String cpf; 
	@NotNull
	private DepartmentEnum department; 
	private boolean enabled;
}