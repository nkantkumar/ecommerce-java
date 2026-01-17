package com.codility;

import java.util.*;
import java.util.stream.*;

public class CollectorsMagic {

    static class Employee {
        String name;
        String department;
        int salary;

        Employee(String name, String department, int salary) {
            this.name = name;
            this.department = department;
            this.salary = salary;
        }

        String getName() { return name; }
        String getDepartment() { return department; }
        int getSalary() { return salary; }
    }

    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
                new Employee("Alice", "Engineering", 100000),
                new Employee("Bob", "Engineering", 95000),
                new Employee("Charlie", "Sales", 80000),
                new Employee("David", "Sales", 75000),
                new Employee("Eve", "HR", 70000)
        );

        // 1. Partitioning - split into two groups
        Map<Boolean, List<Employee>> partitioned = employees.stream()
                .collect(Collectors.partitioningBy(e -> e.getSalary() > 80000));

        System.out.println("High earners: " + partitioned.get(true).size());
        System.out.println("Others: " + partitioned.get(false).size());

        // 2. Grouping with downstream collectors
        Map<String, Double> avgSalaryByDept = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.averagingInt(Employee::getSalary)
                ));

        System.out.println("\nAverage salary by department:");
        avgSalaryByDept.forEach((dept, avg) ->
                System.out.printf("%s: $%.2f%n", dept, avg));

        // 3. Multi-level grouping
        Map<String, Map<Boolean, List<Employee>>> multiLevel = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.partitioningBy(e -> e.getSalary() > 80000)
                ));

        // 4. Collecting statistics
        IntSummaryStatistics salaryStats = employees.stream()
                .collect(Collectors.summarizingInt(Employee::getSalary));

        System.out.println("\nSalary Statistics:");
        System.out.println("Average: $" + salaryStats.getAverage());
        System.out.println("Max: $" + salaryStats.getMax());
        System.out.println("Min: $" + salaryStats.getMin());
        System.out.println("Total: $" + salaryStats.getSum());

        // 5. Custom collector - joining with formatting
        String nameList = employees.stream()
                .map(Employee::getName)
                .collect(Collectors.joining(", ", "Employees: [", "]"));

        System.out.println("\n" + nameList);

        // 6. toMap with merge function
        Map<String, Integer> deptTotalSalary = employees.stream()
                .collect(Collectors.toMap(
                        Employee::getDepartment,
                        Employee::getSalary,
                        Integer::sum  // Merge function for duplicate keys
                ));

        System.out.println("\nTotal salary by department:");
        deptTotalSalary.forEach((dept, total) ->
                System.out.printf("%s: $%d%n", dept, total));
    }
}
