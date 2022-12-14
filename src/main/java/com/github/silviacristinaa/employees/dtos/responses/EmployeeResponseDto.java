package com.github.silviacristinaa.employees.dtos.responses;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class EmployeeResponseDto {
	
	private Long totalActive;
	private Long totalInactive;
	private Page<EmployeeResponseDataDto> employeeResponseDataDto;
}