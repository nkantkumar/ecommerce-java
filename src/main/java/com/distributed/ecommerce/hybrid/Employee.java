package com.distributed.ecommerce.hybrid;

import java.util.*;
import java.time.LocalDate;

public final class Employee {
    private final String id;
    private final String name;
    private final List<String> skills;
    private final Map<String, String> metadata;
    private final LocalDate hireDate;

    private Employee(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "ID cannot be null");
        this.name = Objects.requireNonNull(builder.name, "Name cannot be null");
        this.skills = List.copyOf(builder.skills);
        this.metadata = Map.copyOf(builder.metadata);
        this.hireDate = builder.hireDate;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getSkills() { return skills; }
    public Map<String, String> getMetadata() { return metadata; }
    public LocalDate getHireDate() { return hireDate; }

    // "Modification" methods return new instances
    public Employee withSkill(String skill) {
        List<String> newSkills = new ArrayList<>(this.skills);
        newSkills.add(skill);
        return new Builder()
                .id(this.id)
                .name(this.name)
                .skills(newSkills)
                .metadata(this.metadata)
                .hireDate(this.hireDate)
                .build();
    }

    public Employee withoutSkill(String skill) {
        List<String> newSkills = new ArrayList<>(this.skills);
        newSkills.remove(skill);
        return new Builder()
                .id(this.id)
                .name(this.name)
                .skills(newSkills)
                .metadata(this.metadata)
                .hireDate(this.hireDate)
                .build();
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }



    public static class Builder {
        private String id;
        private String name;
        private List<String> skills = new ArrayList<>();
        private Map<String, String> metadata = new HashMap<>();
        private LocalDate hireDate;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder skill(String skill) {
            this.skills.add(skill);
            return this;
        }

        public Builder skills(List<String> skills) {
            this.skills = new ArrayList<>(skills);
            return this;
        }

        public Builder metadata(Map<String, String> metadata) {
            this.metadata = new HashMap<>(metadata);
            return this;
        }

        public Builder addMetadata(String key, String value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder hireDate(LocalDate hireDate) {
            this.hireDate = hireDate;
            return this;
        }

        public Employee build() {
            return new Employee(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) &&
                Objects.equals(name, employee.name) &&
                Objects.equals(skills, employee.skills) &&
                Objects.equals(metadata, employee.metadata) &&
                Objects.equals(hireDate, employee.hireDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, skills, metadata, hireDate);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", skills=" + skills +
                ", metadata=" + metadata +
                ", hireDate=" + hireDate +
                '}';
    }
}

