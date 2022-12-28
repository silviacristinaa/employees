package com.github.silviacristinaa.employees.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.silviacristinaa.employees.entities.Employee;
import com.github.silviacristinaa.employees.enums.DepartmentEnum;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
	Optional<Employee> findByCpf(String cpf);
	
	@Query("SELECT e FROM Employee e WHERE (:department is null or e.department = :department)"
			+ "and (:enabled is null or e.enabled = :enabled)")
	List<Employee> findByDepartmentAndStatus(
			@Param("department") DepartmentEnum department,
			@Param("enabled") Boolean enabled);
}