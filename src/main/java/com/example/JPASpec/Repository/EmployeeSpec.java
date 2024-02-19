package com.example.JPASpec.Repository;

import com.example.JPASpec.Entity.Departments;
import com.example.JPASpec.Entity.EmpSearch;
import com.example.JPASpec.Entity.Employee;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeSpec implements Specification<Employee> {
    private EmpSearch empSearch;

    public EmployeeSpec(EmpSearch empSearch) {
        this.empSearch = empSearch;
    }

    @Override
    public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Join<Employee, Departments> departmentsJoin = root.join("department", JoinType.LEFT);
        List<Predicate> predicates = new ArrayList<>();
        if (empSearch.getFirstName() != null && !empSearch.getFirstName().isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("firstName"), empSearch.getFirstName()));
        }
        if (empSearch.getDepName() != null && !empSearch.getDepName().isEmpty())
            predicates.add(criteriaBuilder.like(departmentsJoin.get("depName"), empSearch.getDepName()));
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
