package com.claude;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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
        getMaxSalaryByDepartmentAndCity(employees);

        employees.stream()
                .filter(e -> e.getAge() > 25 && e.getProjects().size() > 2)
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.averagingDouble(Employee::getSalary))

                ).entrySet()
                .stream()
                // .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .toList();


    }

    public List<Map.Entry<String, Double>> getTop3DepartmentsByAvgSalary(List<Employee> employees) {
        return employees.stream()
                .filter(e -> e.getAge() > 25 && e.getProjects().size() > 2)
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.averagingDouble(Employee::getSalary)
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .toList();
    }

    public List<Map.Entry<String, Double>> getTop3DepartmentsByAvgSalaryWithLogging(
            List<Employee> employees) {

        return employees.stream()
                .filter(e -> {
                    boolean eligible = e.getAge() > 25 && e.getProjects().size() > 2;
                    if (!eligible) {
                        System.out.println(
                                String.format(
                                        "Filtered out employee: %s (age=%d, projects=%d)",
                                        e.getName(),
                                        e.getAge(),
                                        e.getProjects().size()
                                )
                        );
                    }
                    return eligible;
                })
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.averagingDouble(Employee::getSalary)
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());
    }
    public static Map<String, Map<String, Employee>> getMaxSalaryByDepartmentAndCity(
            List<Employee> employees) {
        var result = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.groupingBy(
                                Employee::getCity,
                                Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary))
                        )


        ));
        result.forEach((k1,n)->
                n.forEach((k2, emp) ->
                        System.out.println(k1 + " "+ k2 +" " + emp)

         ));
        // Accessing safely
        Optional<Employee> engNycTop = Optional.ofNullable(result.get("Engineering"))
                .flatMap(cityMap -> Optional.ofNullable(cityMap.get("NYC")))
                .flatMap(Function.identity());

        engNycTop.ifPresent(emp ->
                System.out.printf("Top Engineering employee in NYC: %s ($%.2f)%n",
                        emp.getName(), emp.getSalary()));
        return Map.of();
    }
    public record BudgetReport(
            double totalBudget,
            long qualifiedEmployees,
            Map<String, Double> budgetByDepartment
    ) {}

    public  BudgetReport calculateRaiseBudgetWithReport(List<Employee> employees){
        LocalDate twoYearsAgo  = LocalDate.now().minusYears(2);
        var deptCounts =  employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.counting()

                ));
        var qualified = employees.stream()
                .filter(e -> e.getJoiningDate().isAfter(twoYearsAgo))
                .filter(e -> deptCounts.getOrDefault(e.getDepartment(), 0L) > 5)
                .filter(e -> e.getProjects().stream().allMatch(p -> p.contains("critical")))
                .toList();
        double totalBudget  = qualified.stream()
                .mapToDouble(e -> e.getSalary()* 0.10)
                .sum();

        Map<String, Double> budgetByDept = qualified.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.summingDouble(e -> e.getSalary() * 0.10)
                ));


        return new BudgetReport(totalBudget, qualified.size(), budgetByDept);
    }
}
