package com.github.silviacristinaa.employees.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class EmployeeStatusRequestDto {
	
	private boolean enabled;
}