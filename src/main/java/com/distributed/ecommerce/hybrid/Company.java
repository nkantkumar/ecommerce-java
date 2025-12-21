package com.distributed.ecommerce.hybrid;

import java.util.List;
import java.util.stream.Collectors;

public final class Company {
    private final String name;
    private final List<Employee> employees;

    public Company(String name, List<Employee> employees) {
        this.name = name;
        // Deep copy if Employee is mutable, or just copy if Employee is immutable
        this.employees = employees.stream()
               // .map(Employee::copy)  // Assuming Employee has a copy method
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Employee> getEmployees() {
        // If Employee is immutable, this is safe
        return employees;

        // If Employee is mutable, need deep copy:
        // return employees.stream()
        //     .map(Employee::copy)
        //     .collect(Collectors.toList());
    }
}
