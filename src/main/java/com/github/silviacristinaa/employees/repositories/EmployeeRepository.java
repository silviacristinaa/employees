package com.github.silviacristinaa.employees.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.silviacristinaa.employees.entities.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
	Optional<Employee> findByCpf(String cpf);
}
