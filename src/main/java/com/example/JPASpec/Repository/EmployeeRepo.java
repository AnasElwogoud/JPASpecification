package com.example.JPASpec.Repository;

import com.example.JPASpec.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee,Long>, JpaSpecificationExecutor<Employee> {
}
