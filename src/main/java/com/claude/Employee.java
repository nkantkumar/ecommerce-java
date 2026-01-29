package com.claude;

import java.time.LocalDate;
import java.util.List;


class Employee {
    private String name;
    private String department;
    private double salary;
    private int age;
    private List<String> projects;
    private String city;
    private LocalDate joiningDate;

    public Employee(String name, String department, double salary, int age, List<String> projects, String city, LocalDate joiningDate) {
        this.name = name;
        this.department = department;
        this.salary = salary;
        this.age = age;
        this.projects = projects;
        this.city = city;
        this.joiningDate = joiningDate;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public double getSalary() {
        return salary;
    }

    public int getAge() {
        return age;
    }

    public List<String> getProjects() {
        return projects;
    }

    public String getCity() {
        return city;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    // constructors, getters, setters
}