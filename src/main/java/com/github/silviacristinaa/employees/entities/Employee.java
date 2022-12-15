package com.github.silviacristinaa.employees.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.github.silviacristinaa.employees.enums.DepartmentEnum;

import lombok.Getter;
import lombok.Setter;

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