package com.github.silviacristinaa.employees.entities;

import com.github.silviacristinaa.employees.enums.DepartmentEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	@Column(nullable = false)
	private String name; 
	@Column(nullable = false, unique = true, length = 11)
	private String cpf;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private DepartmentEnum department; 
	@Column(nullable = false)
	private boolean enabled;
}