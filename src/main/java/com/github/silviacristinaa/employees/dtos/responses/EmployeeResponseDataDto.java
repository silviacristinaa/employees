package com.github.silviacristinaa.employees.dtos.responses;

import com.github.silviacristinaa.employees.enums.DepartmentEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class EmployeeResponseDataDto {
	
	private Long id;
	private String name;
	private String cpf; 
	private DepartmentEnum department;
	private boolean enabled;
}