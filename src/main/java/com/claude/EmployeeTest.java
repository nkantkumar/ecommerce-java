package com.claude;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeTest {
    public static void main(String[] args) {

        List<Employee> employees = List.of(
                new Employee("Alice", "Engineering", 95000, 28,
                        List.of("ProjectA", "ProjectB", "Critical-X"),
                        "NYC", LocalDate.of(2022, 3, 15)),
                new Employee("Bob", "Engineering", 110000, 32,
                        List.of("ProjectA", "ProjectC", "Critical-Y", "ProjectD"),
                        "SF", LocalDate.of(2021, 6, 20)),
                new Employee("Charlie", "Sales", 75000, 26,
                        List.of("Deal1", "Deal2"),
                        "NYC", LocalDate.of(2023, 1, 10)),
                new Employee("Charls", "Sales", 75000, 26,
                        List.of("Deal1", "Deal2"),
                        "NYC", LocalDate.of(2023, 1, 10)),
                new Employee("Clark", "Sales", 75000, 26,
                        List.of("Deal1", "Deal2"),
                        "NYC", LocalDate.of(2023, 1, 10)));

        employees.stream()
                .filter(e -> e.getAge() > 25 && e.getProjects().size() >2)
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.averagingDouble(Employee::getSalary))

                ).entrySet()
                .stream()
               // .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .sorted((Comparator<? super Map.Entry<String, Double>>) Map.Entry.comparingByValue().reversed())
                .limit(3)
                .toList();


    }
}
