package com.github.silviacristinaa.employees.dtos.responses;

import com.github.silviacristinaa.employees.enums.DepartmentEnum;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmployeeResponseDto {
	
	private Long id;
	private String name;
	private String cpf; 
	private DepartmentEnum department;
	private boolean enabled;
}
