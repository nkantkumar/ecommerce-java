Problem 1: Employee Analytics System
Setup
javaclass Employee {
private String name;
private String department;
private double salary;
private int age;
private List<String> projects;
private String city;
private LocalDate joiningDate;

    // Constructor, getters, setters omitted for brevity
}

// Test data generator
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
// ... more employees
);

Solution 1.1: Top 3 Departments by Average Salary (Filtered)
java/**
* Business Logic:
* - Filter: age > 25 AND projects > 2
* - Group by department
* - Calculate average salary per department
* - Sort descending by average
* - Take top 3
*
* Complexity: O(n log n) due to sorting
* Memory: O(d) where d = number of departments
  */
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

// Alternative with explicit peek for debugging in production
public List<Map.Entry<String, Double>> getTop3DepartmentsByAvgSalaryWithLogging(
List<Employee> employees) {

    return employees.stream()
        .filter(e -> {
            boolean eligible = e.getAge() > 25 && e.getProjects().size() > 2;
            if (!eligible) {
                log.debug("Filtered out employee: {} (age={}, projects={})", 
                         e.getName(), e.getAge(), e.getProjects().size());
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
Staff Engineer Notes:

Used averagingDouble instead of manual sum/count - more readable and handles edge cases
Two-phase stream approach: first collect/aggregate, then sort - necessary because sorting requires materialization
limit(3) applied after sorting - O(n log n) is unavoidable here, but we could optimize with a heap-based approach if department count is huge
Consider caching this result if called frequently


Solution 1.2: Nested Map - Max Salary Employee per Department-City
java/**
* Creates nested map structure: Department -> City -> Employee
*
* Key Challenge: groupingBy twice, then reducing to max salary employee
*
* Performance: O(n) - single pass with nested grouping
* Memory: O(d × c × e) worst case, typically O(d × c) with one employee per group
  */
  public Map<String, Map<String, Employee>> getMaxSalaryByDepartmentAndCity(
  List<Employee> employees) {

  return employees.stream()
  .collect(Collectors.groupingBy(
  Employee::getDepartment,
  Collectors.groupingBy(
  Employee::getCity,
  Collectors.collectingAndThen(
  Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary)),
  opt -> opt.orElse(null) // Handle empty groups gracefully
  )
  )
  ));
  }

// Production-ready version with better null handling
public Map<String, Map<String, Optional<Employee>>> getMaxSalaryByDepartmentAndCitySafe(
List<Employee> employees) {

    return employees.stream()
        .collect(Collectors.groupingBy(
            Employee::getDepartment,
            Collectors.groupingBy(
                Employee::getCity,
                Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary))
            )
        ));
}

// Usage example
public void demonstrateUsage() {
Map<String, Map<String, Optional<Employee>>> result =
getMaxSalaryByDepartmentAndCitySafe(employees);

    // Accessing safely
    Optional<Employee> engNycTop = Optional.ofNullable(result.get("Engineering"))
        .flatMap(cityMap -> Optional.ofNullable(cityMap.get("NYC")))
        .flatMap(Function.identity());
    
    engNycTop.ifPresent(emp -> 
        System.out.printf("Top Engineering employee in NYC: %s ($%.2f)%n", 
                         emp.getName(), emp.getSalary()));
}
Staff Engineer Notes:

collectingAndThen transforms the Optional<Employee> to Employee (with null fallback)
Second version keeps Optional in the structure - safer but more verbose to use
Nested groupingBy is memory-intensive - for very large datasets, consider custom Collector or database aggregation
Thread-safe if using parallel streams (groupingBy uses ConcurrentHashMap internally when parallel)


Solution 1.3: Budget Calculation with Complex Filtering
java/**
* Multi-criteria budget calculation:
* 1. Joined in last 2 years
* 2. Department has >5 employees
* 3. Has critical project
*
* Approach: Two-pass - first identify qualifying departments, then calculate budget
  */
  public double calculateRaiseBudget(List<Employee> employees) {
  LocalDate twoYearsAgo = LocalDate.now().minusYears(2);

  // First pass: identify departments with >5 employees
  Set<String> largeDepartments = employees.stream()
  .collect(Collectors.groupingBy(
  Employee::getDepartment,
  Collectors.counting()
  ))
  .entrySet()
  .stream()
  .filter(entry -> entry.getValue() > 5)
  .map(Map.Entry::getKey)
  .collect(Collectors.toSet());

  // Second pass: calculate budget for qualified employees
  return employees.stream()
  .filter(e -> e.getJoiningDate().isAfter(twoYearsAgo))
  .filter(e -> largeDepartments.contains(e.getDepartment()))
  .filter(e -> e.getProjects().stream()
  .anyMatch(p -> p.contains("Critical")))
  .mapToDouble(Employee::getSalary)
  .map(salary -> salary * 0.10) // 10% raise
  .sum();
  }

// Optimized single-pass version using custom collector
public double calculateRaiseBudgetOptimized(List<Employee> employees) {
LocalDate twoYearsAgo = LocalDate.now().minusYears(2);

    record EmployeeWithDeptCount(Employee employee, long deptCount) {}
    
    // Count departments while iterating
    Map<String, Long> deptCounts = employees.stream()
        .collect(Collectors.groupingBy(
            Employee::getDepartment,
            Collectors.counting()
        ));
    
    return employees.stream()
        .filter(e -> e.getJoiningDate().isAfter(twoYearsAgo))
        .filter(e -> deptCounts.getOrDefault(e.getDepartment(), 0L) > 5)
        .filter(e -> e.getProjects().stream()
                      .anyMatch(p -> p.contains("Critical")))
        .mapToDouble(e -> e.getSalary() * 0.10)
        .sum();
}

// With detailed reporting
public record BudgetReport(
double totalBudget,
long qualifiedEmployees,
Map<String, Double> budgetByDepartment
) {}

public BudgetReport calculateRaiseBudgetWithReport(List<Employee> employees) {
LocalDate twoYearsAgo = LocalDate.now().minusYears(2);

    Map<String, Long> deptCounts = employees.stream()
        .collect(Collectors.groupingBy(
            Employee::getDepartment,
            Collectors.counting()
        ));
    
    List<Employee> qualified = employees.stream()
        .filter(e -> e.getJoiningDate().isAfter(twoYearsAgo))
        .filter(e -> deptCounts.getOrDefault(e.getDepartment(), 0L) > 5)
        .filter(e -> e.getProjects().stream()
                      .anyMatch(p -> p.contains("Critical")))
        .toList();
    
    double totalBudget = qualified.stream()
        .mapToDouble(e -> e.getSalary() * 0.10)
        .sum();
    
    Map<String, Double> budgetByDept = qualified.stream()
        .collect(Collectors.groupingBy(
            Employee::getDepartment,
            Collectors.summingDouble(e -> e.getSalary() * 0.10)
        ));
    
    return new BudgetReport(totalBudget, qualified.size(), budgetByDept);
}
Staff Engineer Notes:

Two-pass approach vs single-pass tradeoff: two-pass is clearer but less efficient
Used mapToDouble to avoid boxing overhead with Double stream
Record-based report structure (Java 16+) for immutable, type-safe results
For production: add validation (non-null checks, negative salary handling)
Consider BigDecimal for financial calculations to avoid floating-point precision issues


Solution 1.4: Find Employee Pairs with Common Projects
java/**
* Complex pair-finding with multiple criteria
*
* Approach: Cartesian product filtering
* Complexity: O(n²) - unavoidable for all-pairs comparison
*
* Optimization consideration: For n > 10,000, consider:
* - Index by city first (reduces search space)
* - Parallel streams
* - Inverted index on projects
    */
    public record EmployeePair(
    Employee employee1,
    Employee employee2,
    Set<String> commonProjects
    ) {}

public List<EmployeePair> findCollaboratingPairs(List<Employee> employees) {
return IntStream.range(0, employees.size())
.boxed()
.flatMap(i -> IntStream.range(i + 1, employees.size())
.mapToObj(j -> new int[]{i, j}))
.map(indices -> {
Employee e1 = employees.get(indices[0]);
Employee e2 = employees.get(indices[1]);
return new AbstractMap.SimpleEntry<>(e1, e2);
})
.filter(pair -> pair.getKey().getCity().equals(pair.getValue().getCity()))
.filter(pair -> !pair.getKey().getDepartment()
.equals(pair.getValue().getDepartment()))
.map(pair -> {
Set<String> common = findCommonProjects(
pair.getKey().getProjects(),
pair.getValue().getProjects()
);
return new EmployeePair(pair.getKey(), pair.getValue(), common);
})
.filter(pair -> pair.commonProjects().size() >= 2)
.toList();
}

private Set<String> findCommonProjects(List<String> projects1, List<String> projects2) {
Set<String> set1 = new HashSet<>(projects1);
return projects2.stream()
.filter(set1::contains)
.collect(Collectors.toSet());
}

// Optimized version using spatial indexing
public List<EmployeePair> findCollaboratingPairsOptimized(List<Employee> employees) {
// Index by city first to reduce search space
Map<String, List<Employee>> byCity = employees.stream()
.collect(Collectors.groupingBy(Employee::getCity));

    return byCity.values().stream()
        .flatMap(cityEmployees -> findPairsInCity(cityEmployees).stream())
        .toList();
}

private List<EmployeePair> findPairsInCity(List<Employee> cityEmployees) {
return IntStream.range(0, cityEmployees.size())
.boxed()
.flatMap(i -> IntStream.range(i + 1, cityEmployees.size())
.mapToObj(j -> new int[]{i, j}))
.parallel() // Safe because we're creating new objects
.map(indices -> {
Employee e1 = cityEmployees.get(indices[0]);
Employee e2 = cityEmployees.get(indices[1]);

            if (e1.getDepartment().equals(e2.getDepartment())) {
                return null;
            }
            
            Set<String> common = findCommonProjects(
                e1.getProjects(), e2.getProjects()
            );
            
            return common.size() >= 2 
                ? new EmployeePair(e1, e2, common) 
                : null;
        })
        .filter(Objects::nonNull)
        .toList();
}

// Most optimized: inverted index approach
public List<EmployeePair> findCollaboratingPairsWithIndex(List<Employee> employees) {
// Build inverted index: project -> list of employees
Map<String, List<Employee>> projectIndex = employees.stream()
.flatMap(emp -> emp.getProjects().stream()
.map(proj -> new AbstractMap.SimpleEntry<>(proj, emp)))
.collect(Collectors.groupingBy(
Map.Entry::getKey,
Collectors.mapping(Map.Entry::getValue, Collectors.toList())
));

    // For each project, generate pairs of employees on that project
    Map<EmployeePairKey, Set<String>> pairProjects = new HashMap<>();
    
    projectIndex.forEach((project, emps) -> {
        for (int i = 0; i < emps.size(); i++) {
            for (int j = i + 1; j < emps.size(); j++) {
                Employee e1 = emps.get(i);
                Employee e2 = emps.get(j);
                
                if (e1.getCity().equals(e2.getCity()) &&
                    !e1.getDepartment().equals(e2.getDepartment())) {
                    
                    EmployeePairKey key = new EmployeePairKey(e1, e2);
                    pairProjects.computeIfAbsent(key, k -> new HashSet<>())
                                .add(project);
                }
            }
        }
    });
    
    return pairProjects.entrySet().stream()
        .filter(entry -> entry.getValue().size() >= 2)
        .map(entry -> new EmployeePair(
            entry.getKey().e1,
            entry.getKey().e2,
            entry.getValue()
        ))
        .toList();
}

// Helper class for pair deduplication
private record EmployeePairKey(Employee e1, Employee e2) {
EmployeePairKey {
// Ensure consistent ordering for deduplication
if (System.identityHashCode(e1) > System.identityHashCode(e2)) {
Employee temp = e1;
e1 = e2;
e2 = temp;
}
}
}
Staff Engineer Notes:

Three implementations with different complexity tradeoffs:

Naive O(n²): Clear but slow
City-indexed O(n² / c): Better with many cities
Inverted index O(n × p): Best for many employees, moderate projects


Parallel stream in optimized version is safe because no shared mutable state
Real-world consideration: for >100k employees, move to database with SQL joins
Record types provide immutable, value-based pair representation


Solution 1.5: Salary Distribution Report by Department
java/**
* Complex aggregation with custom grouping logic
*
* Output: Map<Department, Map<SalaryBracket, Percentage>>
  */
  public enum SalaryBracket {
  BRACKET_0_50K(0, 50000),
  BRACKET_50K_100K(50000, 100000),
  BRACKET_100K_150K(100000, 150000),
  BRACKET_150K_PLUS(150000, Double.MAX_VALUE);

  private final double min;
  private final double max;

  SalaryBracket(double min, double max) {
  this.min = min;
  this.max = max;
  }

  public static SalaryBracket fromSalary(double salary) {
  return Arrays.stream(values())
  .filter(bracket -> salary >= bracket.min && salary < bracket.max)
  .findFirst()
  .orElseThrow();
  }

  public boolean contains(double salary) {
  return salary >= min && salary < max;
  }
  }

public record DepartmentSalaryDistribution(
String department,
long totalEmployees,
Map<SalaryBracket, Long> counts,
Map<SalaryBracket, Double> percentages
) {}

public List<DepartmentSalaryDistribution> getSalaryDistribution(
List<Employee> employees) {

    // Group by department first
    Map<String, List<Employee>> byDept = employees.stream()
        .collect(Collectors.groupingBy(Employee::getDepartment));
    
    return byDept.entrySet().stream()
        .map(entry -> {
            String dept = entry.getKey();
            List<Employee> deptEmployees = entry.getValue();
            long total = deptEmployees.size();
            
            // Count employees in each bracket
            Map<SalaryBracket, Long> counts = deptEmployees.stream()
                .collect(Collectors.groupingBy(
                    e -> SalaryBracket.fromSalary(e.getSalary()),
                    Collectors.counting()
                ));
            
            // Calculate percentages
            Map<SalaryBracket, Double> percentages = Arrays.stream(SalaryBracket.values())
                .collect(Collectors.toMap(
                    bracket -> bracket,
                    bracket -> (counts.getOrDefault(bracket, 0L) * 100.0) / total
                ));
            
            return new DepartmentSalaryDistribution(dept, total, counts, percentages);
        })
        .sorted(Comparator.comparing(DepartmentSalaryDistribution::department))
        .toList();
}

// Alternative: Single-pass custom collector
public Map<String, Map<SalaryBracket, Double>> getSalaryDistributionCompact(
List<Employee> employees) {

    // First get counts
    Map<String, Map<SalaryBracket, Long>> counts = employees.stream()
        .collect(Collectors.groupingBy(
            Employee::getDepartment,
            Collectors.groupingBy(
                e -> SalaryBracket.fromSalary(e.getSalary()),
                Collectors.counting()
            )
        ));
    
    // Then convert to percentages
    Map<String, Long> deptTotals = employees.stream()
        .collect(Collectors.groupingBy(
            Employee::getDepartment,
            Collectors.counting()
        ));
    
    return counts.entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> {
                String dept = entry.getKey();
                long total = deptTotals.get(dept);
                
                return entry.getValue().entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        bracketEntry -> (bracketEntry.getValue() * 100.0) / total
                    ));
            }
        ));
}

// Pretty printing for reports
public void printSalaryDistributionReport(List<Employee> employees) {
List<DepartmentSalaryDistribution> distributions =
getSalaryDistribution(employees);

    System.out.println("Salary Distribution Report");
    System.out.println("=".repeat(80));
    
    distributions.forEach(dist -> {
        System.out.printf("%n%s (Total: %d employees)%n", 
                         dist.department(), dist.totalEmployees());
        System.out.println("-".repeat(80));
        
        Arrays.stream(SalaryBracket.values()).forEach(bracket -> {
            long count = dist.counts().getOrDefault(bracket, 0L);
            double percentage = dist.percentages().get(bracket);
            
            System.out.printf("  %-20s: %3d employees (%5.1f%%) %s%n",
                bracket.name(),
                count,
                percentage,
                "█".repeat((int) (percentage / 2)) // Simple bar chart
            );
        });
    });
}
Staff Engineer Notes:

Enum-based bracket classification is type-safe and extensible
Two-phase approach: count first, then calculate percentages (clearer than single custom collector)
Record types for immutable report data structures
Considered adding statistical measures (median, mode) but kept focused on requirements
Production enhancement: add caching layer if this report runs frequently
Consider exporting to JSON/CSV for integration with BI tools


Problem 2: E-Commerce Order Processing
Setup
javaclass Order {
private String orderId;
private String customerId;
private LocalDateTime orderDate;
private List<OrderItem> items;
private OrderStatus status;
private String region;

    public double getTotalAmount() {
        return items.stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity() * 
                                (1 - item.getDiscount()))
            .sum();
    }
}

class OrderItem {
private String productId;
private String productName;
private String category;
private int quantity;
private double price;
private double discount; // 0.0 to 1.0
}

enum OrderStatus {
PENDING, SHIPPED, DELIVERED, CANCELLED
}

Solution 2.1: Top 5 Spending Customers (Last 3 Months)
java/**
* Multi-criteria customer ranking
*
* Business Rules:
* - Last 3 months only
* - Exclude CANCELLED orders
* - Only orders with >3 items
* - Rank by total spend
    */
    public record CustomerSpending(
    String customerId,
    double totalSpent,
    long orderCount,
    double averageOrderValue
    ) {}

public List<CustomerSpending> getTop5SpendingCustomers(List<Order> orders) {
LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);

    Map<String, List<Order>> validOrdersByCustomer = orders.stream()
        .filter(o -> o.getOrderDate().isAfter(threeMonthsAgo))
        .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
        .filter(o -> o.getItems().size() > 3)
        .collect(Collectors.groupingBy(Order::getCustomerId));
    
    return validOrdersByCustomer.entrySet().stream()
        .map(entry -> {
            String customerId = entry.getKey();
            List<Order> customerOrders = entry.getValue();
            
            double total = customerOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
            
            long count = customerOrders.size();
            double avg = total / count;
            
            return new CustomerSpending(customerId, total, count, avg);
        })
        .sorted(Comparator.comparingDouble(CustomerSpending::totalSpent).reversed())
        .limit(5)
        .toList();
}

// Performance-optimized version using single-pass aggregation
public List<CustomerSpending> getTop5SpendingCustomersOptimized(List<Order> orders) {
LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);

    // Custom collector to compute all stats in one pass
    class SpendingAccumulator {
        double totalSpent = 0;
        long orderCount = 0;
        
        void add(Order order) {
            totalSpent += order.getTotalAmount();
            orderCount++;
        }
        
        void merge(SpendingAccumulator other) {
            totalSpent += other.totalSpent;
            orderCount += other.orderCount;
        }
        
        CustomerSpending toResult(String customerId) {
            return new CustomerSpending(
                customerId,
                totalSpent,
                orderCount,
                orderCount > 0 ? totalSpent / orderCount : 0
            );
        }
    }
    
    return orders.stream()
        .filter(o -> o.getOrderDate().isAfter(threeMonthsAgo))
        .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
        .filter(o -> o.getItems().size() > 3)
        .collect(Collectors.groupingBy(
            Order::getCustomerId,
            Collector.of(
                SpendingAccumulator::new,
                SpendingAccumulator::add,
                (a, b) -> { a.merge(b); return a; }
            )
        ))
        .entrySet().stream()
        .map(e -> e.getValue().toResult(e.getKey()))
        .sorted(Comparator.comparingDouble(CustomerSpending::totalSpent).reversed())
        .limit(5)
        .toList();
}
Staff Engineer Notes:

Custom accumulator avoids multiple passes over the data
Collector.of() for complex aggregation logic
Consider adding percentile analysis (P50, P90, P99 spending) for deeper insights
For real-time dashboards, maintain materialized views or use time-series database


Solution 2.2: Category Statistics
javapublic record CategoryStats(
String category,
double totalRevenue,
double averageOrderValue,
ProductInfo mostPopularProduct,
Map<String, Double> revenueByMonth
) {}

public record ProductInfo(
String productId,
String productName,
long totalQuantitySold
) {}

public Map<String, CategoryStats> getCategoryStatistics(List<Order> orders) {
// Flatten orders to items
Stream<OrderItemWithDate> itemStream = orders.stream()
.filter(o -> o.getStatus() != OrderStatus.CANCELLED)
.flatMap(order -> order.getItems().stream()
.map(item -> new OrderItemWithDate(item, order.getOrderDate())));

    Map<String, List<OrderItemWithDate>> byCategory = itemStream
        .collect(Collectors.groupingBy(i -> i.item().getCategory()));
    
    return byCategory.entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> calculateCategoryStats(entry.getKey(), entry.getValue())
        ));
}

private record OrderItemWithDate(OrderItem item, LocalDateTime orderDate) {}

private CategoryStats calculateCategoryStats(
String category,
List<OrderItemWithDate> items) {

    // Total revenue
    double totalRevenue = items.stream()
        .mapToDouble(i -> i.item().getPrice() * i.item().getQuantity() * 
                         (1 - i.item().getDiscount()))
        .sum();
    
    // Average order value - group by implicit order
    // (This is approximate since we flattened; ideally track order IDs)
    double avgOrderValue = totalRevenue / items.size();
    
    // Most popular product by quantity
    ProductInfo mostPopular = items.stream()
        .collect(Collectors.groupingBy(
            i -> i.item().getProductId(),
            Collectors.summingLong(i -> i.item().getQuantity())
        ))
        .entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(e -> {
            OrderItem sample = items.stream()
                .filter(i -> i.item().getProductId().equals(e.getKey()))
                .findFirst()
                .map(OrderItemWithDate::item)
                .orElseThrow();
            return new ProductInfo(e.getKey(), sample.getProductName(), e.getValue());
        })
        .orElse(null);
    
    // Revenue by month
    Map<String, Double> revenueByMonth = items.stream()
        .collect(Collectors.groupingBy(
            i -> i.orderDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
            Collectors.summingDouble(i -> 
                i.item().getPrice() * i.item().getQuantity() * 
                (1 - i.item().getDiscount()))
        ));
    
    return new CategoryStats(
        category,
        totalRevenue,
        avgOrderValue,
        mostPopular,
        revenueByMonth
    );
}

// Better approach: preserve order context
public Map<String, CategoryStats> getCategoryStatisticsAccurate(List<Order> orders) {
List<Order> validOrders = orders.stream()
.filter(o -> o.getStatus() != OrderStatus.CANCELLED)
.toList();

    // For each category, compute stats
    Set<String> categories = validOrders.stream()
        .flatMap(o -> o.getItems().stream())
        .map(OrderItem::getCategory)
        .collect(Collectors.toSet());
    
    return categories.stream()
        .collect(Collectors.toMap(
            Function.identity(),
            category -> calculateCategoryStatsAccurate(validOrders, category)
        ));
}

private CategoryStats calculateCategoryStatsAccurate(
List<Order> orders,
String category) {

    // Filter orders that contain items from this category
    List<Order> relevantOrders = orders.stream()
        .filter(o -> o.getItems().stream()
            .anyMatch(item -> item.getCategory().equals(category)))
        .toList();
    
    // Total revenue from this category across all orders
    double totalRevenue = relevantOrders.stream()
        .flatMap(o -> o.getItems().stream())
        .filter(item -> item.getCategory().equals(category))
        .mapToDouble(item -> item.getPrice() * item.getQuantity() * 
                            (1 - item.getDiscount()))
        .sum();
    
    // Average order value (for orders containing this category)
    double avgOrderValue = relevantOrders.stream()
        .mapToDouble(Order::getTotalAmount)
        .average()
        .orElse(0);
    
    // Most popular product
    Map<String, Long> productQuantities = relevantOrders.stream()
        .flatMap(o -> o.getItems().stream())
        .filter(item -> item.getCategory().